package org.ooc.nodes.others;

import java.io.IOException;
import java.util.ArrayList;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.ClassReference;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.functions.FunctionCall;
import org.ooc.nodes.functions.MemberFunctionCall;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.preprocessor.Define;
import org.ooc.nodes.preprocessor.DefineSymbolRef;
import org.ooc.nodes.types.Type;
import org.ooc.parsers.TypeParser;
import org.ooc.structures.Function;
import org.ooc.structures.FunctionVariable;
import org.ooc.structures.PointerToFunction;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;

/**
 * A Name is a valid C identifier (letter/underline + number/letter/underlines at will).
 * If it's in the syntax tree, it hasn't been resolved yet to a VariableAccess, or
 * ClassReference, for example. It's a transition node for hard to parse situations (ie.
 * needing multiple passes), and may stay as-is if it's not resolved at all
 * @author Amos Wenger
 */
public class Name extends RawCode {

	// FIXME haha this is ugly
	// why do we need a count here? if we knew every identifier, we could
	// resolve every name every time. But unfortunately, we can't. Because
	// we include stuff from C and headers are a nightmare and a half to parse.
	int count = 5;
	
	/**
	 * Default constructor
	 * @param location
	 * @param content
	 */
    public Name(FileLocation location, String content) {
        super(location, content);
    }

    
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    	
    	if(getParent() == null) {
    		return; // Null parent, okay.
    	}
    	SourceContext context = manager.getContext();
    	
    	Scope scope = getParent().getNearest(Scope.class);
    	
    	//System.out.println(location+" assembling name "+content);

    	if(scope != null) {
	    	Function func = scope.getImplementation(context, content); 
	    	if(func != null && !(func instanceof PointerToFunction)) {
	    		ClassDef classDef = getParent().getNearest(ClassDef.class);
	    		if((classDef == null || classDef.clazz.isCover || classDef.clazz != func.clazz)
	    				&& func.args.list.isEmpty()) {
	    			
	    			SyntaxNode next = getNext();
					if(next instanceof Parenthesis) {
						Parenthesis paren = (Parenthesis) next;
						if(!paren.nodes.isEmpty()) {
							manager.errAndFail("[Name] Trying to call function "+func.getSimpleName()+" with arguments "
									+paren+", but this function has no arguments!", paren);
					
						}
						paren.drop();
	    			}
					FunctionCall call = new FunctionCall(location, content);
					replaceWith(manager, call);
					//System.out.println("replaced with a function call");
	    			return;
	    			
	    		} // else: we're in a getter, no point in calling ourselves, of course.
	    	}
    	}
    	
    	Define defineSymbol = context.getDefineSymbol(content);
    	if(defineSymbol != null) {
    		DefineSymbolRef dsf = new DefineSymbolRef(location, defineSymbol);
			replaceWith(manager, dsf);
			//System.out.println("replaced with a define symbol");
			return;
    	}
		
		SyntaxNode prev = getParent().getPrev(this);
        if(prev != null && prev instanceof Dot) {
        	//System.out.println("prev isn't null and it's a dot, returning..");
            return;
        }
		
		ClassDef nearestClassDef = getParent().getNearest(ClassDef.class);
		if(nearestClassDef != null && (content.equals("This") || nearestClassDef.clazz.simpleName.equals(content) || nearestClassDef.clazz.fullName.equals(content))) {
			ClassReference classRef = new ClassReference(location, nearestClassDef);
			replaceWith(manager, classRef);
			//System.out.println("replaced with a ClassReference (we're in this class, actually)");
			return;
	    }

        SyntaxNode next = getParent().getNext(this);
        ClassDef classDef = manager.getContext().getClassDef(content);
        if(classDef != null) {
        	if(next instanceof Name) {
                Name nextName = (Name) next;
                getParent().remove(next);
                VariableDecl decl = new VariableDecl(location, new Variable(new Type(location, getParent(), content, 0), nextName.content));
                replaceWith(manager, decl);
                //System.out.println("Replaced with a variable declaration of name "+nextName.content);
            } else {
                ClassReference classRef = new ClassReference(location, classDef);
                replaceWith(manager, classRef);
                //System.out.println("replaced with a ClassReference");
            }
        	return;
        }
        
        if(scope != null) {
            Variable variable = scope.getVariable(content);
            if(variable != null) {
                VariableAccess access = new VariableAccess(location, variable);
                replaceWith(manager, access);
				//System.out.println("replaced with variableaccess");
                return;
            }

            if(nearestClassDef != null) {
				VariableAccess thisAccess = new VariableAccess(location, nearestClassDef.clazz.getThis());
				if(content.equals("this")) {
					replaceWith(manager, thisAccess);
					//System.out.println("replaced with variableaccess to this");
					return;
				}
				
				Variable member = nearestClassDef.getMember(manager.getContext(), content);
				if(member != null) {
					replaceWith(manager, new MemberAccess(location, thisAccess, member));
					//System.out.println("replaced with memberAccess");
					return;
				}
				
				if(nearestClassDef.clazz.hasUnmangledFunction(content)) {
					
					// All of this is wayy deprecated. Since 0.2, a function name without parenthesis calls it.
					
					//if(getNext() instanceof Parenthesis) {
					
						MemberFunctionCall memberFuncCall = new MemberFunctionCall(location, content, null, thisAccess);
						replaceWith(manager, memberFuncCall);
						//System.out.println("replaced with memberFunctionCall");
						
					/*} else {
						
						Function pointedFunction = nearestClassDef.clazz.getUnmangledFunctions(content).get(0);
						FunctionDef def = nearestClassDef.getFunctionDef(pointedFunction);
						if(!(def.function.isStatic)) {
							manager.warn("Passing a pointer to function which is not static. 'this' parameter could be garbage when called from C code.", this);
						}
						MemberAccess classVarAccess = new MemberAccess(location, nearestClassDef.clazz.getThis(), nearestClassDef.clazz.getClassVariable());
						Variable funcPointerVariable = new FunctionVariable(pointedFunction);
						MemberAccess memberAccess = new MemberAccess(location,
								classVarAccess, funcPointerVariable);
						replaceWith(manager, memberAccess);
						//System.out.println("replaced with memberAccess");
						
					}*/
					return;
					
				}
				
            }
            
            Function pointedFunction = scope.getImplementation(manager.getContext(), content,
            		new TypedArgumentList(location, new ArrayList<Variable>()));
            if(pointedFunction != null) {

				Variable funcPointerVariable = new FunctionVariable(pointedFunction);
				VariableAccess varAccess = new VariableAccess(location, funcPointerVariable);
				replaceWith(manager, varAccess);
				//System.out.println("replaced with access to function pointer");
				return;
            	
            }
            
            if(manager.getContext().getTypeDef(content) != null
            		|| manager.getContext().getEnum(content) != null
            		|| TypeParser.isValidType(content)) {
            	replaceWith(manager, new Type(location, getParent(), content));
            	//System.out.println("replaced with type reference!");
            	return;
            }
            
        } else {
        	//System.out.println("null scope!");
        }
        
        //System.out.println("didn't resolve to anything!");
        if(count-- > 0) {
        	manager.queue(this, "Name not resolved to anything!");
        }
        
    }

    
    @Override
	public void writeToCSource(Appendable a) throws IOException {

        writeWhitespace(a);
        super.writeToCSource(a);
    }

}
