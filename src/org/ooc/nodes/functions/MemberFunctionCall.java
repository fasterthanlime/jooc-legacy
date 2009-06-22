package org.ooc.nodes.functions;

import java.io.IOException;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.types.Type;
import org.ooc.structures.PointerToFunction;
import org.ubi.FileLocation;

/**
 * A call to a member function of an object, e.g. "myObject.myFunction(arg)"
 * 
 * @author Amos Wenger
 */
public class MemberFunctionCall extends FunctionCall {

	/**
	 * In a call to "myObject.myFunction(arg)", "myObject" is the variable
	 * accessed by the following VariableAccess
	 */
	public VariableAccess access;
	
	/**
	 * Default constructor. Calls function 'name' in object referenced by 'access'.
	 * If nodes is non-null, they are treated as arguments to the call (they can
	 * also be added later)
	 * @param location
	 * @param name the name of the called function
	 * @param nodes arguments, if any (things between the parenthesis. Args must
	 * be separated by parenthesis).
	 * @param access see {@link MemberFunctionCall#access}
	 */
	public MemberFunctionCall(FileLocation location, String name, List<SyntaxNode> nodes, VariableAccess access) {
		super(location, name);
		this.access = access;
		if(nodes != null) {
			this.addAll(nodes);
		}
	}

	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		if(manager.isDirty(access)) {
			manager.queue(access, "Dirty access for member function call, queueing it.");
			manager.queue(this, "Dirty access for member function call, queueing it.");
			//System.err.println("Access is dirty, requeuing...");
			return;
		}
		
		Type type = access.getType();
		if(!type.isFlat()) {
			throw new CompilationFailedError(location, "Trying to call function "
					+ name + getArgsRepr()+" in array/pointer type "+type.getDescription());
		}
        ClassDef classDef = manager.getContext().getClassDef(type.name);
        if(classDef == null) {
        	manager.queue(this, "Trying to call " + type + "." + name + getArgsRepr() + " but type " + type + " can't be found. Did you forget to import "+type+" ?");
            return;
        }
        
		this.clazz = classDef.clazz;
		TypedArgumentList tal = new TypedArgumentList(this);
		impl = classDef.getImplementation(manager.getContext(), name, tal);
		if(impl == null) {
		    manager.queue(this, "Trying to call " + access + "." + name + getArgsRepr()
		    		+ " but class " + clazz.fullName + " doesn't have such a function. It has: " + clazz.getFunctionListRepr());
		    return;
		}

    }

	@Override
    public String getDescription() {
    	return "member function call to "+access+"->"+name+getDescription(nodes)+location;
    }
	
	@Override
	protected boolean isSpaced() {
		return true;
	}

	@Override
	public void writeToCSource(Appendable a) throws IOException {

		writeWhitespace(a);
		
		if(access.getType().isCover) {
			
			a.append(impl.getMangledName(clazz));
			
			a.append('(');
		    if(!(impl instanceof PointerToFunction)) {
			    access.writeToCSource(a);
			    if(this.nodes.size() > 0) {
			        a.append(", ");
			    }
		    }
		    for(SyntaxNode node: nodes) {
		    	node.writeToCSource(a);
		    }
		    a.append(')');
			
		} else {
		
			access.writeToCSource(a);
			a.append("->");
			
			if(!(impl instanceof PointerToFunction)) {
				a.append("class->");
			}
			
			if(impl == null) {
				a.append(name);
			} else {
	            a.append(impl.getMangledName(null));
			}
			
		    a.append('(');
		    if(!(impl instanceof PointerToFunction)) {
			    access.writeToCSource(a);
			    if(this.nodes.size() > 0) {
			        a.append(", ");
			    }
		    }
		    for(SyntaxNode node: nodes) {
		    	node.writeToCSource(a);
		    }
		    a.append(')');
		    
		}
	    
		
	}

	/**
	 * @return the access to the object of which a member method is called
	 */
	public SyntaxNode getAccess() {

		return access;
		
	}

}
