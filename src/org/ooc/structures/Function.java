package org.ooc.structures;

import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.types.Type;

/**
 * A function. FunctionDef is the corresponding node class.
 * This class takes care of writing  correct C code for function
 * pointers, assignment, and does also name mangling, etc.
 * @author Amos Wenger
 */
public class Function implements Field {

	/**
	 * Functions are prefixed in generated C code, to allow fields and functions
	 * to have the same name (e.g. ArrayList.size and ArrayList.size() in the sdk)
	 */
    public static final String FUNCTIONS_PREFIX = "__";
    
    private String name;
    
    /**
     * The return type of this function.
     */
    public Type returnType;
    
    /**
     * The class this function is member of. Can be null.
     */
    public Clazz clazz ;
    
    /**
     * A list of this function's arguments and their types. 
     */
    public TypedArgumentList args;
    
    /**
     * An abstract function must be implemented by any concrete (=non-abstract)
     * subclass.
     */
    public boolean isAbstract;
    
    /**
     * If true, will be written in the C code without any mangling. Useful in some
     * cases, e.g. function wrappers. 
     */
	public boolean isUnmangled;
	
	/**
	 * If true, the callback hack will be used. For more info, see @link {@link CallbackFeature}
	 */
	public boolean isCallback;
	
	/**
	 * Add the 'static' keyword in front of the function when generating C.
	 */
	public boolean isStatic;

	/**
	 * Default constructor.
	 * @param name the name of the function
	 * @param returnType its return type
	 * @param clazz the class this function belongs to, or null.
	 * @param args
	 */
    public Function(String name, Type returnType, Clazz clazz, TypedArgumentList args) {
        this(name, returnType, clazz, args, false);
    }

    private Function(String name, Type returnType, Clazz clazz, TypedArgumentList args, boolean isAbstract) {
        this.name = name;
        this.returnType = returnType;
        this.clazz = clazz;
        this.args = args;
        this.isAbstract = isAbstract;
        this.isCallback = false;
        this.isUnmangled = false;
        this.isStatic = name.equals("new"); // new is static by default
        if(clazz != null) {
            clazz.functions.add(this);
        }
    }

    /** 
     * @return the simple name of this function, ignoring mangling etc. e.g. "myfunc"
     */
    public String getSimpleName() {

        return name;

    }
    
    /**
     * @return the name of this function, prefixed by FUNCTIONS_PREFIX (ie. the field name in the struct), 
     * e.g. "_myfunc"
     */
    public String getPrefixedName() {
		
		return isUnmangled ? getSimpleName() : FUNCTIONS_PREFIX+getSimpleName();
		
	}

    /**
     * @return the name of this function, correctly mangled, as used by the C
     * code generator, e.g. "my_super_package_MyClass_myfunc_char_int_int"
     */
    public String getMangledName() {

    	return getMangledName(clazz);

    }

    /**
     * @return the name of this function, correctly mangled, as used by the C
     * code generator. Acts as if this function is a member function of destClazz.
     */
    public String getMangledName(Clazz destClazz) {

    	if(isUnmangled) {
    		return getSimpleName();
    	}
    	
        if(getSimpleName().equals("main") && this.clazz == null) {
            return "main"; // Don't mess with the main.
        }

        StringBuilder builder =  new StringBuilder();
        builder.append(FUNCTIONS_PREFIX); // Functions begin with "_" to avoid confusion with fields
        if(destClazz != null) {
            builder.append(destClazz.underName);
            builder.append(SyntaxNode.SEPARATOR);
        }
        builder.append(getSimpleName());
        for(Variable var: args.list) {
            builder.append(SyntaxNode.SEPARATOR);
            builder.append(var.type.toMangledString());
        }
        return builder.toString();

    }

    /**
     * @return a new Function as if it was implemented in 'destClass' instead
     */
    public Function copyInClass(Clazz destClass) {
    	
        return new Function(getSimpleName(), returnType, destClass, args, isAbstract);
        
    }

    /**
     * Write a function pointer declaration in a form suitable for a C compiler.
     */
    
    public boolean writeDeclaration(Appendable a, Clazz destClazz) throws IOException {
    	
    	if(isStatic) {
    		return false; // Don't write static functions
    	}
    	
    	a.append(returnType.toString());
    	
        a.append(" (*");
        a.append(getMangledName(null));
        a.append(")(");
        writeArgs(a, destClazz);
        a.append(')');
        return true;
        
    }

    /**
     * Writes the prototype of this function to 'a', as if it was a member function of
     * 'destClazz' in a form suitable for a C compiler =) 
     * Used in the C code generation process
     */
    public boolean writePrototype(Appendable a, Clazz destClazz) throws IOException {
    	
    	boolean isConstructor = isConstructor();
		if(isConstructor && destClazz.isAbstract) {
    		return false;
    	}
        
    	if(isStatic) {
    		a.append("extern ");
    	} else if(shouldWriteStatic()) {
    		// TODO it should be documented that unmangled implies non-static
    		// Yep, C and Java have quasi opposite meanings of 'static', but this actually is correct.
    		a.append("static ");
    	}
    	
    	if(isConstructor) {
            a.append(destClazz.underName);            
        } else {
            a.append(returnType.toString());
        }
    	
        a.append(" ");
        a.append(getMangledName(destClazz));
        a.append("(");
        writeArgs(a, destClazz);
        a.append(")");
        
        return true;
        
    }

	private boolean shouldWriteStatic() {
		
		//return !isConstructor() && !name.equals("main") && !isUnmangled;
		return false;
		
	}
    
    /**
     * Writes the prototype of this function to 'a', in a form suitable for a human
     * (e.g. useful in development/debugging tools)
     */
    public void writeSimplePrototype(Appendable a, Clazz destClazz) throws IOException {
	
    	a.append(getSimpleName());
        a.append("(");
        writeSimpleArgs(a, destClazz);
        a.append(")");
		
	}
    
    /**
     * Write the arguments (without parenthesis) of this function to appendable 'a',
     * in a form suitable for a human (e.g. useful in development/debugging tools)
     */
    public void writeSimpleArgs(Appendable a, Clazz destClazz) throws IOException {
    	
        int count = 0;

        for(Variable arg: args.list) {
            if(destClazz != null && arg.type.isInstance(destClazz)) {
                if(count == 0) {
                    a.append(destClazz.simpleName);
                } else {
                    a.append(arg.type.name);
                }
            } else {
                a.append(arg.type.toString());
            }
            if(++count < args.list.size()) {
                a.append(", ");
            }
        }
        
    }

    /**
     * Write all arguments (without parenthesis) to appendable 'a', in a form suitable
     * for C export, as if this function was a member function of 'destClazz'.
     * Note: 'destClazz' can be null, in which case this function is treated as a non-member function.  
     */
    public void writeArgs(Appendable a, Clazz destClazz) throws IOException {
    	
        int count = 0;

        if(destClazz != null && !isStatic) {
            
        	destClazz.getType().writeToCSource(a);
            
            if(!args.list.isEmpty()) {
                a.append(", ");
            }
        }
        for(Variable arg: args.list) {
        	
        	a.append(arg.type.toString());
            if(++count < args.list.size()) {
                a.append(", ");
            }
            
        }
        
    }

    
    @Override
	public String toString() {
    	
        return getMangledName(null);
        
    }

    /**
     * @return true if this function is named 'name'
     */
	public boolean isNamed(String name) {

		return getSimpleName().equals(name);
		
	}

	/**
	 * @return a simple (=human-friendly) representation of this function's
	 * prototype. Can be used in development tools for visualization.
	 */
	public String getSimplePrototype() {

		Type.resolveCheckEnabled = false;
		
		StringBuilder builder = new StringBuilder();
		try {
			writeSimplePrototype(builder, clazz);
		} catch(Exception e) {
			e.printStackTrace();
			Type.resolveCheckEnabled = true;
			return getSimpleName();
		}
		
		Type.resolveCheckEnabled = true;
		return builder.toString();
		
	}

	/**
	 * @param context
	 * @return the FunctionDef corresponding to this Function.
	 */
	public FunctionDef getFunctionDef(SourceContext context) {
		
		return clazz.getClassDef().getFunctionDef(this);
		
	}

	
	public boolean isStatic() {
		
		return isStatic;
		
	}

	
	public void setStatic(boolean isStatic) {

		this.isStatic = isStatic;
		
	}

	/**
	 * @return true if this function is a constructor, e.g. new(), false otherwise.
	 */
	public boolean isConstructor() {

		return getSimpleName().equals("new");
		
	}

}
