package org.ooc.structures;

import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.Cover;
import org.ooc.nodes.types.Type;
import org.ubi.FileLocation;

/**
 * Contains every bit of information about an ooc class.
 * The name 'Clazz' has been chosed to avoid a name clash with java.lang.Class
 *
 * @author Amos Wenger
 */
public class Clazz {

	/**
	 * If true, then this class is abstract, which means it cannot be instanciated.
	 * It can be subclassed with 'extends', though. It allows to have a 'base' class
	 * and cast subclasses around.
	 */
    public boolean isAbstract;
    
    /**
     * The name of the class, without the package name, e.g. "MyClass" 
     */
    public final String simpleName;
    
    /**
     * The name of the class, prefixed by the package name with dots, e.g.
     * "org.mypackage.MyClass"
     */
    public final String fullName;
    
    /**
     * The name that is used to identify the instance structure in C. Basically it's 'fullName'
     * with '_' instead of '.'
     */
    public final String underName;
    
    /**
     * The name that is used to identify the class structure in C. Basically it's 'fullName'
     * with '_' instead of '.' and with '__class' added at the end.
     */
    public final String underNameClass;
    
    /**
     * The name of the variable in the object structure which refers to the class structure
     */
    public final String classVariableName;
    
    /**
     * The class we have subclassed from. Can be null, if it's a root class. 
     * (Note: in ooc, not every class is a subclass of Object)
     */
    private Clazz zuper;
    
    /**
     * The fullName of our super class (or the empty string if we are a root class)
     */
    public final String zuperFullName;
    
    /**
     * A list of our class' member functions.
     */
    public ArrayList<Function> functions;
    
    /**
     * A list of our class' member variables, that we call fields.
     */
    public ArrayList<Member> members;
    
    /**
	 * true if this class is in fact a cover.
	 * @see Cover
	 */
    public boolean isCover;
    
    /**
     * As a Variable to 'this' of a particular class is often useful, this is
     * a cached version of such a Variable, which can be easily shared, instead
     * of creating several instances.
     */
    private Variable thisVariable;
    private Variable classVariable;
    private Variable staticClassVariable;
    
    /**
     * The type of our class. Has an arrayLevel and pointerLevel of 0, of course.
     * The name of the type is equal to this class' undername, which allows
     * natural handling of variable declarations, either of basic types (int, float, ..)
     * or class types (MyClass, ...)
     */
	private Type type;
	private Type classType;
	
	/**
	 * The classDef associated to this clazz
	 */
	private ClassDef classDef;
	
	/**
	 * Default constructor.
	 * @param fullName
	 * @param zuperFullName
	 */
    public Clazz(final String fullName, final String zuperFullName) {

        this.functions = new ArrayList<Function>();
        this.members = new ArrayList<Member>();
        int index = fullName.lastIndexOf('.');
		this.simpleName = (index == -1 ? fullName : fullName.substring(index + 1));
        this.fullName = fullName;
        
        this.underName = fullName.replace('.', '_');
        this.underNameClass = underName + "__class";
        this.classVariableName = underNameClass + "";
        
        this.type = new Type(new FileLocation("<system:Clazz>", -1, -1, -1), null, underName);
        type.clazz = this;
        type.isResolved = true;
        this.classType = new Type(type.location, null, underNameClass);
        classType.metaClazz = this;
        classType.isCover = isCover;
        classType.isValid = true;
        classType.isResolved = true;
        
        this.zuperFullName = zuperFullName;
        this.isAbstract = false;
        this.thisVariable = new Variable(getType(), "this");
        this.classVariable = new Variable(getClassType(), "class");
        this.staticClassVariable = new Variable(getClassType(), "_classInstance", true);

    }
    
    /**
     * Set this classe
     * @param classDef
     */
    public void setClassDef(ClassDef classDef) {
    	
    	assert this.classDef == null;
    	this.classDef = classDef;
    	
    }

	/**
     * @return a Variable named 'this' which has the type of the instance structure
     * of this Class.
     */
    public Variable getThis() {
    	
        return thisVariable;
        
    }
    
    /**
     * @return a Variable named 'class' which has the type of the class structure
     * of this Class.
     */
	public Variable getClassVariable() {

		return classVariable;
		
	}
	
	/**
	 * @return a Variable in the object structure which refers to the class structure. 
	 */
	public Variable getStaticClassVariable() {
		
		return staticClassVariable;
		
	}
    
    /**
     * Failsafe getZuper. Return the super-class of this class, or null if
     * it can't be found.
     * @param context
     * @return the super-class, or null.
     */
    public Clazz getZuperOrNull(SourceContext context) {
    	
    	try {
    		return getZuper(context);
    	} catch(CompilationFailedError e) {
    		return null;
    	}
    	
    }
    
    /**
     * Return the super-class, or throw a CompilationFailedError if not found.
     * @param context
     * @return the super-class.
     */
    public Clazz getZuper(SourceContext context) {

    	if(this.zuperFullName.isEmpty()) { // No super class.
    		return null;
    	}
    	
        if(zuper == null) {
            ClassDef zuperDef = context.getClassDef(this.zuperFullName);
            
            if(zuperDef == null) {
            	ClassDef def = context.getClassDef(this.fullName);
            	throw new CompilationFailedError(null, "Class ["+this.zuperFullName+"], super class of ["+this.fullName+"] "
            			+"cannot be resolved (did you forget to import it?) "
            			+(def == null ? "<unknown location>" : def.location));
            }
            
			zuper = zuperDef.clazz;
        }
        return zuper;

    }
    
    /**
     * Get a function by its mangled name
     * @param context
     * @param mangledName
     * @return
     */
	public Function getFunctionRecursive(SourceContext context, String mangledName) {
		
        Clazz current = this;
        while(current != null) { 
        	for(Function func: current.functions) {
                if(func.getMangledName(null).equals(mangledName)) {
                    return func;
                }
            }
            current = current.getZuperOrNull(context);
        }
        return null;
		
	}
    
    /**
     * Test for the existence of a function by its simple name.
     * @param unmangledFuncName e.g. 'myFunc' for a function myFunc(int i, char c)
     * @return true if this class has at least one function named 'funcName'
     */
    public boolean hasUnmangledFunction(String unmangledFuncName) {

        return !getUnmangledFunctions(unmangledFuncName).isEmpty();

    }

    /**
     * Search for functions by their unmangled names
     * @param unmangledFuncName e.g. 'myFunc' for a function myFunc(int i, char c)
     * @return the list of functions that have this unmangled name.
     */
    public List<Function> getUnmangledFunctions(String unmangledFuncName) {
    	
        ArrayList<Function> list = new ArrayList<Function>();
        for(Function func: functions) {
            if(func.getSimpleName().equals(unmangledFuncName)) {
                list.add(func);
            }
        }
        return list;

    }

    /**
     * Search for functions by their unmangled names, including in super-classes 
     * @param context
     * @param unmangledFuncName
     * @return
     */
    public List<Function> getUnmangledFunctionsRecursive(SourceContext context, String unmangledFuncName) {

        ArrayList<Function> list = new ArrayList<Function>();
        Clazz current = this;
        while(current != null) {
            list.addAll(current.getUnmangledFunctions(unmangledFuncName));
            current = current.getZuperOrNull(context);
        }
        return list;

    }

    /**
     * @param context
     * @return the list of the mangled names of all functions in this class,
     * except constructors
     */
    public List<String> getMangledNamesExceptNew(SourceContext context) {

        List<String> contracts = new ArrayList<String>();
        Clazz current = this;

        while(current != null) {
            for(Function func: current.functions) {
                if(func.getSimpleName().equals("new")) { continue; }
                if(!contracts.contains(func.getMangledName(null))) {
                    contracts.add(func.getMangledName(null));
                }
            }
            current = current.getZuper(context);
        }

        return contracts;

    }

    /**
     * @return the type corresponding to the instance structure of this class
     */
    public Type getType() {
    	
        return type;
        
    }
    
    /**
     * @return the type corresponding to the class structure of this class
     */
    public Type getClassType() {
    	
        return classType;
        
    }

    /**
     * @param name
     * @return true if 'name' is this class's simple name, full name, or under name.
     */
	public boolean isNamed(String name) {
		
		return simpleName.equals(name) || fullName.equals(name) || underName.equals(name);
		
	}
	
	@Override
	public String toString() {

		return fullName;
		
	}

	/**
	 * @return a human-friendly representation of the functions in this class
	 */
	public String getFunctionListRepr() {

		StringBuilder builder = new StringBuilder();
		int i = 0;
		try {
			for(Function func: functions) {
				func.writeSimplePrototype(builder, this);
				if(++i < functions.size()) {
					builder.append(", ");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return builder.toString();
		
	}

	/**
	 * @return true if candidate is a subclass or the same class as this class.
	 */
	public boolean isSubclassOf(Clazz candidate, SourceContext context) {

		Clazz current = this;
		while(current != null) {
			if(current == candidate) {
				return true;
			}
			current = current.getZuperOrNull(context);
		}
		return false;
		
	}

	/**
	 * @return true if it has a super-class, false if it's a root class.
	 */
	public boolean hasSuper() {
		
		return !zuperFullName.isEmpty();
		
	}

	/**
	 * @param context
	 * @return the "level" of this class. E.g. 0 if it's a root class (=no super-class), 
	 * 1 if it's a subclass of a root class, 2 if it's a subclass of a subclass
	 * of a root class, etc.
	 */
	public int getLevel(SourceContext context) {
 
		Clazz zuper = getZuperOrNull(context);
		return (zuper == null ? 0 : zuper.getLevel(context) + 1);
		
	}
	
	/**
	 * @return the classDef associated to this clazz
	 */
	public ClassDef getClassDef() {
		return classDef;
	}

}
