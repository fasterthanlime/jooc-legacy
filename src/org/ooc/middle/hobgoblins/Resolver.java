package org.ooc.middle.hobgoblins;

import java.io.IOException;

import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.interfaces.MustBeResolved.Response;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.walkers.Opportunist;
import org.ooc.middle.walkers.SketchyNosy;


public class Resolver implements Hobgoblin {

	protected static final int MAX = 4, SAFE_MAX = 1024;
	boolean running = false;
	boolean restarted = false;
	public boolean fatal = false;
	
	static int restartCount = 0;
	
	public BuildParams params;
	public Module module;
	
	public boolean process(Module module, final BuildParams params) throws IOException {
		
		this.module = module;
		this.params = params;
		
		// FIXME this is a hack to allow early removal of ghost type-arguments
		// in TypeDecls. see their resolve() method 
		{
			NodeList<Node> stack = new NodeList<Node>(Token.defaultToken);
			stack.push(module);
			for(TypeDecl typeDecl: module.getTypes().values()) {
				typeDecl.resolve(stack, this, false);
			}
		}
		
		SketchyNosy nosy = SketchyNosy.get(new Opportunist<Node>() {
			public boolean take(Node node, NodeList<Node> stack) throws IOException {
				if(node instanceof MustBeResolved) {
					MustBeResolved must = (MustBeResolved) node;
					if(!must.isResolved()) {
						Response res = Response.OK;
						try {
							res = must.resolve(stack, Resolver.this, fatal);
						} catch(OocCompilationError e) {
							if(params.veryVerbose) {
								e.printStackTrace();
							} else {
								throw e;
							}
						}
						if(res == Response.LOOP) {
							if(fatal) {
								System.out.println(must+" has LOOPed in fatal round.");
							}
							//System.out.println(must+" has LOOPed.");
							running = true;
						} else if(res == Response.RESTART) {
							if(fatal) {
								System.out.println(must+" has RESTARTed in fatal round.");
							}
							if(params.veryVerbose) {
								restartCount++;
								System.out.println("("+restartCount+") [ "+must.getClass().getSimpleName()+" ]\t\t"+must+" has RESTARTed.");
							}
								
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
		
		return running;
		
	}
	
	@Override
	public String toString() {
		return module.getFullName();
	}
	
	public boolean isFatal() {
		return fatal;
	}
	
}
