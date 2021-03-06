package org.ooc.middle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ooc.frontend.BuildParams;
import org.ooc.frontend.model.Module;
import org.ooc.middle.hobgoblins.Checker;
import org.ooc.middle.hobgoblins.Resolver;
import org.ooc.middle.hobgoblins.Unwrapper;
import org.ubi.CompilationFailedError;

/**
 * The Tinkerer(TM) handles all the work that there's to be done
 * between parsing and generating, e.g.
 *  - Resolving symbols (functions, variables)
 *  - Ensuring encapsulation isn't violated (e.g. unauthorized access
 *  to protected members)
 *  - Huh.. other things, I guess, see all classes in this package 
 * 
 * @author Amos Wenger
 */
public class Tinkerer {

	public void process(List<Module> modules, BuildParams params) throws IOException {

		for(Module module: modules) {
			new Unwrapper().process(module, params);
		}
		
		List<Resolver> resolvers = new ArrayList<Resolver>();
		for(Module module: modules) {
			Resolver resolver = new Resolver();
			resolver.module = module;
			resolvers.add(resolver);
		}
		
		int round = 0;
		while(resolvers.size() > 0) {
			
			round += 1;
			if(params.veryVerbose) System.out.println("\n=======================================\n\nTinkerer, round "
					+round+", "+resolvers.size()+" left ("+resolvers+")");
			
			Iterator<Resolver> iter = resolvers.iterator();
			while(iter.hasNext()) {
	
				Resolver resolver = iter.next();
				
				// returns true = dirty, must do again
				if(resolver.process(resolver.module, params)) continue;
			
				if(params.veryVerbose) System.out.println("Module "+resolver.module.getFullName()+" finished resolving.");
				
				// done? check it and remove it from the processing queue
				new Checker().process(resolver.module, params);
				
				iter.remove();
				
			}
			
			if(round == params.blowup) {
				for(Resolver resolver: resolvers) {
					resolver.fatal = true;
				}
			}
			if(round > params.blowup) {
				throw new CompilationFailedError(null, "Tinkerer going round in circles. Remaining modules = "+resolvers);
			}
			
		}
		
	}

}
