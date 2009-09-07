package org.ooc.middle.hobgoblins;

import java.io.IOException;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.interfaces.MustBeResolved.Response;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.walkers.Opportunist;
import org.ooc.middle.walkers.SketchyNosy;


public class Resolver implements Hobgoblin {

	protected static final int MAX = 1, SAFE_MAX = 1024;
	boolean running = false;
	boolean restarted = false;
	boolean fatal = false;
	
	public BuildParams params;
	public Module module;
	
	@Override
	public void process(Module module, BuildParams params) throws IOException {
		
		this.module = module;
		this.params = params;
		
		SketchyNosy nosy = SketchyNosy.get(new Opportunist<Node>() {
			@Override
			public boolean take(Node node, NodeList<Node> stack) throws IOException {
				if(node instanceof MustBeResolved) {
					MustBeResolved must = (MustBeResolved) node;
					if(!must.isResolved()) {
						Response res = must.resolve(stack, Resolver.this, fatal);
						if(res == Response.LOOP) {
							running = true;
						} else if(res == Response.RESTART) {
							restarted = true;
							running = true;
							return false;
						}
					}
				}
				return true;
			}
		});
		
		int count = 0, safeCount = 0;
		running = true;
		while(running) {
			if(count > MAX || safeCount > SAFE_MAX) {
				fatal = true;
				nosy.start().visit(module);
				throw new OocCompilationError(module, module, "Resolver is running in circles. Abandoning. (count = "
						+count+"/"+MAX+", safeCount = "+safeCount+"/"+SAFE_MAX+")");
			}
			running = false;
			nosy.start().visit(module);
			safeCount++;
			if(restarted) {
				restarted = false;
			} else {
				count++;
			}
		}
		
	}
	
}
