package org.ooc.nodes.functions;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.interfaces.Typed;
import org.ooc.nodes.others.Comma;
import org.ooc.nodes.others.RawCode;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.types.Type;
import org.ooc.structures.AssignedVariableAlias;
import org.ooc.structures.Variable;
import org.ooc.structures.VariableAlias;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * A typed argument list represents e.g. the list of arguments to a function,
 * either in its definition or in a call.
 * Since these arguments are usually typed (or, if not recognized, of UNKNOWN type),
 * it makes it easier to know which version of a function to use. 
 *
 * @author Amos Wenger
 */
public class TypedArgumentList extends SyntaxNode {
	
	/** Variables contained by this list. All variables are typed. */
    public List<Variable> list;

    /**
     * Create a new empty TypedArgumentList
     * @param location
     */
    public TypedArgumentList(FileLocation location) {

    	super(location);
    	list = new ArrayList<Variable>();
    	
	}
    
    /**
     * Default constructor from a list of arguments
     * @param location
     * @param args
     */
    public TypedArgumentList(FileLocation location, List<Variable> args) {
    	
        super(location);
        this.list = args;
        
    }
    
    /**
     * Default constructor from a syntax node list
     * @param location
     * @param group
     */
    public TypedArgumentList(SyntaxNodeList group) {
    	
        this(group.location, new ArrayList<Variable>());
        boolean shouldBeComma = false;
        for(SyntaxNode node: group.nodes) {
            if(shouldBeComma) {
                if(node instanceof Comma) {
                	shouldBeComma = !shouldBeComma;
                } else {
                    continue; // We've probably stumbled upon an unknown at the previous step, anyway
                }
            } else {
                if(node instanceof Comma) {
                    throw new Error("Got Comma while expecting.. anything but Comma, at "+node.location);
                } else if(node instanceof Typed) {
                    Typed typed = (Typed) node;
                    list.add(new Variable(typed.getType(), ""));
                } else {
                	// These warnings really get me on my nerves <>
                    if(node instanceof RawCode) {
                    	//System.err.println("TypedArgumentList expected something typed, but got a RawCode, which content is '"+((RawCode) node).content+"'"+node.location);
                    } else {
                    	//System.err.println("TypedArgumentList expected something typed, but got a "+node.getClass().getSimpleName()+" instead."+node.location);
                    }
                    list.add(new Variable(Type.UNRESOLVED, "")); // Won't correspond to anything anyway
                }
                shouldBeComma = !shouldBeComma;
            }
        }
        
    }

	@Override
    public TypedArgumentList clone() {
        return new TypedArgumentList(location, new ArrayList<Variable>(list));
    }

    @Override
    public void writeToCSource(Appendable a) throws IOException {
        a.append('(');
        int count = 0;
        for(Variable arg: list) {
            a.append(arg.toString());
            if(++count < list.size()) {
                a.append(", ");
            }
        }
        a.append(')');
    }

    /**
     * Read a typed argument list from a source reader
     * @param context
     * @param tokens
     * @return
     * @throws EOFException
     * @throws SyntaxError
     */
    public static TypedArgumentList read(SourceContext context) throws EOFException {
    	
    	SourceReader reader = context.reader;
    	
    	// TODO check more thorougly the correctness of an argument list (we now have the means)
    	
        reader.skipWhitespace();
        if(!reader.matches("(", true)) {
        	return null;
        }

        ClassDef classDef = context.getNearest(ClassDef.class);
        List<Variable> args = new ArrayList<Variable>();
        while(!reader.matches(")", true)) {

        	reader.skipWhitespace();
        	
        	if(reader.matches("=", true)) {
        		
        		reader.skipWhitespace();
        		FileLocation location = reader.getLocation();
        		String name = reader.readName();
        		if(name.isEmpty()) {
        			if(reader.matches("*", true)) {
        				throw new CompilationFailedError(reader.getLocation(), 
        						"The (=*) syntax has been removed from ooc since v0.2, " +
        						"as it was considered dangerous. Use (=field1, =field2, =field3) instead.");
        			}
					throw new CompilationFailedError(reader.getLocation(), "Missing field name after the '=' in a function's argument list");
        		}
				args.add(new AssignedVariableAlias(name, location));
        		
        	} else {
        		
        		if(classDef != null) {

        			reader.skipWhitespace();
        			FileLocation location = reader.getLocation();
        			int mark = reader.mark();
        			String name = reader.readName();
					Variable member = classDef.getMember(context, name);
        			if(member != null) {
        				args.add(new VariableAlias(name, location));
        				continue;
        			}
					reader.reset(mark);
        			
        		}
        	
            	Type type = Type.read(context, reader);
            	if(type == null) {
            		throw new CompilationFailedError(reader.getLocation(), "Expected a type!");
            	}
                reader.skipWhitespace();
                String name = reader.readName();
                if(name.isEmpty()) {
                	throw new CompilationFailedError(reader.getLocation(), "Expected name after type '"+type.getDescription()+"' in function definition");
                }
                reader.skipWhitespace();
                args.add(new Variable(type, name));
                
        	}
            
            if(reader.matches(",", true)) {
                // Well, continue
            }
            
        }

        return new TypedArgumentList(reader.getLocation(), args);
        
    }
    
    @Override
    protected void assembleImpl(AssemblyManager manager) {
    
    	boolean allTypesResolved = true;
    	
    	for(Variable arg: list) {
    		if(!arg.type.isResolved) {
    			allTypesResolved = false;
    			manager.queue(arg.type, "Can't resolve type "+arg.type.name+" of argument "+arg.getName());
    		}
    	}
    	
    	if(allTypesResolved) {
    		freeze(manager);
    	} else {
    		manager.queue(this, "Waiting on types to assemble");
    	}
    	
    }

    /**
     * Search for an argument name in this list
     * @param name the name to search
     * @return the index of the name in the list, or -1 if not found.
     */
    public int indexOf(String name) {
        int index = -1;
        int i = 0;
        for(Variable arg: list) {
            if(arg.getName().equals(name)) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }
    
    @Override
    public String toString() {
    
    	Type.resolveCheckEnabled = false;
    	String string = super.toString();
    	Type.resolveCheckEnabled = true;
    	
    	return string;
    	
    }

}
