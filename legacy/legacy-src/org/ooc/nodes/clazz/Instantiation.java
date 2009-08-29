package org.ooc.nodes.clazz;

import java.io.EOFException;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.functions.FunctionCall;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

/**
 * Instantiation of a class, e.g.
 * <code>
 * MyObject o = new Object();
 * </code>
 *
 * @author Amos Wenger
 */
public class Instantiation extends FunctionCall {

    protected String clazzName;

    protected Instantiation(FileLocation location, String typeName) {
    	 
        super(location, typeName+"_new");
        clazzName = typeName;
        
    }
    
    /**
     * Read an instantiation from a source reader
     * @param reader
     * @return
     * @throws SyntaxError
     * @throws EOFException
     */
    public static void read(SourceContext context) throws EOFException {
    	
    	SourceReader reader = context.reader;
    	reader.skipWhitespace();
    	FileLocation location = reader.getLocation();
    	
    	String typeName = reader.readName();
    	// We don't check if typeName is empty, because it's actually allowed:
    	// in this case, the compiler must guess which type is meant from the context
    	// e.g.:
    	// ArrayList list = new;
    	// ArrayList list2 = new(10); // a capacity of 10
    	// or:
    	// Window window;
    	// window = new(1024, 768);
    	
    	Instantiation instantiation = new Instantiation(location, typeName);
    	
    	reader.skipWhitespace();
    	if(reader.matches("(", true)) {
    		context.open(instantiation);
    	} else {
			context.add(instantiation);
    	}
        
    }

    
    @Override
	protected void assembleImpl(AssemblyManager manager) {

        if(!assembleAll(manager)) {
        	return;
        }
        
        if(clazzName.isEmpty()) {
        	SyntaxNode prev = getPrev();
        	if(prev instanceof Assignment) {
        		SyntaxNode prev2 = prev.getPrev();
        		if(prev2 instanceof VariableDecl) {
        			VariableDecl decl = (VariableDecl) prev2;
        			clazzName = decl.variable.type.name;
        		} else if(prev2 instanceof VariableAccess) {
        			VariableAccess access = (VariableAccess) prev2;
        			clazzName = access.getType().name;
        		} else {
        			manager.queue(this, "new without a type is only legal after an Assignment of a variable declaration" +
                			" Here, we can't figure out the type. Try 'new T(arg, arg2)' where T is the type you want to instanciate.");
        		}
        	} else {
        		manager.errAndFail("new without a type is only legal after an Assignment." +
            			" Here, we can't figure out the type. Try 'new T(arg, arg2)' where T is the type you want to instanciate.", this);
        	}
        }

        ClassDef classDef = manager.getContext().getClassDef(clazzName);
        if(classDef == null) {
            manager.queue(this, "Trying to instantiate unknown class "+clazzName+", did you forget to import it?");
            return;
        } else if(classDef.clazz.isAbstract) {
            manager.errAndFail("Trying to instantiate abstract class "+clazzName, this);
            return;
        } else {
        	clazz = classDef.clazz;
        }
        
        TypedArgumentList tal = new TypedArgumentList(this);
        
        impl = classDef.getImplementation(manager.getContext(), "new", tal);
        if(impl == null) {
        	Type.resolveCheckEnabled = false;
            String message = "Constructor "
            +clazzName+".new"+tal+" not found";
            Type.resolveCheckEnabled = true;
            manager.queue(this, message);
            return;
        }
        
        if(impl.clazz != classDef.clazz) {
        	Type.resolveCheckEnabled = false;
            String message = "Constructor "+clazzName+".new"+tal
            +" doesn't exist. It exists in super-class "+impl.clazz.simpleName
            +", but constructors aren't inherited automatically.";
            Type.resolveCheckEnabled = true;
            manager.errAndFail(message, this);	
            return;
        }
        
        lock();
        
    }

}
