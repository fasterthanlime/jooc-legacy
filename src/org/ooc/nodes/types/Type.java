package org.ooc.nodes.types;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.array.ArrayDecl;
import org.ooc.nodes.array.Subscript;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.Cover;
import org.ooc.nodes.operators.Star;
import org.ooc.nodes.others.Name;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.parsers.TypeParser;
import org.ooc.structures.Clazz;
import org.ooc.structures.Variable;
import org.ubi.FileLocation;
import org.ubi.SourceReader;
import org.ubi.SyntaxError;

/**
 * The usage of a type in the code, e.g. "int" or "MyClass"
 *
 * @author Amos Wenger
 */
public class Type extends SyntaxNode {

	/** Used when the type of something is still a mistery, ie. needs to be assembled more */
	public static final Type UNKNOWN = new Type(new FileLocation("<system>", 0, 0, 0), null, "<unknown type>");
	
	/** Used when the type simply can't be determined yet by ooc (e.g. headers parsing not implemented yet) */
	public static final Type UNRESOLVED = new Type(new FileLocation("<system>", 0, 0, 0), null, "<unresolved type>");
	
	/** Temporary hack for the Object <=> (void*) relationship. */
	public static final Type OBJECT = new Type(new FileLocation("<system>", 0, 0, 0), null, "Object", 1);

	/** Void type */
	public static final Type VOID = Type.baseType("Void");
	
	/** Temporary hack until the compiler can resolve *every* type, especially in external function definitions */
	public static boolean resolveCheckEnabled = true;
	
	/**
	 * true if this class is in fact a cover.
	 * @see Cover
	 */
	public boolean isCover;
	
	/** Set to true when the type is resolved */
	public boolean isResolved;
    
	/** The class corresponding to this type, if there's one (e.g. if it's not a base type such as "int") */
	public Clazz clazz;
	
	/** The class this type is the 'class structure' of, also called meta-class */
	public Clazz metaClazz;
	
	/** The name of the type, such as "int" or "MyClass" */
	public String name;
    private final int pointerLevel;
    private final int arrayLevel;

	/** true if should not be checked for validity */
	public boolean isValid;

	/**
	 * Default constructor, specifying the name
	 * @param location
	 * @param manager
	 * @param name
	 */
    public Type(FileLocation location, SyntaxNodeList context, String name) {
    	
        this(location, context, name, 0);
        
    }

    /**
     * Default constructor, specifying the name and the pointer level
     * @param location
     * @param manager
     * @param name
     * @param pointerLevel
     */
    public Type(FileLocation location, SyntaxNodeList context, String name, int pointerLevel) {
    	
        this(location, context, name, pointerLevel, 0);
        
    }

    /**
     * Default constructor, specifying the name, the pointer level, and the array level
     * @param location
     * @param manager
     * @param name
     * @param pointerLevel
     * @param arrayLevel
     */
    public Type(FileLocation location, SyntaxNodeList context, String name, int pointerLevel, int arrayLevel) {	
    	
        super(location);
        this.name = name;
        this.pointerLevel = pointerLevel;
        this.arrayLevel = arrayLevel;
        this.isResolved = false;
        setContext(context);
        this.clazz = null;
        
    }

    /**
     * @see writeDeclaration
     */
    @Override
    public void writeToCSource(Appendable a) throws IOException {
    	
    	if(resolveCheckEnabled && !isResolved) {
    		throw new Error("Trying to write a TypeUsage "+location+" of '"+name+"' that's not been assembled!");
    	}
    	
    	writeName(a);
        writeStars(a);
        writeBrackets(a);
        
    }
    
    /**
     * Write correctly a variable declaration. It doesn't yield the same results
     * as writeToCSource(), because C needs the brackets to be after the name, not
     * between the type and the name. So writeToCSource will output "type[] name" while
     * writeDeclaration will output "type name[]"
     * @param a
     * @param variableName
     * @throws IOException
     */
    public void writeDeclaration(Appendable a, String variableName) throws IOException {

    	writeName(a);
    	writeStars(a);
		a.append(' ');
		a.append(variableName);
		writeBrackets(a);
		
	}

	private void writeBrackets(Appendable a) throws IOException {
		if(arrayLevel > 0) {
	        for(int i = 0; i < arrayLevel; i++) {
	            a.append("[]");
	        }
        }
	}

	private void writeStars(Appendable a) throws IOException {
		if(pointerLevel > 0) {
            a.append(' ');
            for(int i = 0; i < pointerLevel; i++) {
                a.append('*');
            }
        }
	}

	private void writeName(Appendable a) throws IOException {
		
		if(clazz != null) {
    		if(clazz.isCover || isCover) {
    			a.append(clazz.simpleName);
    		} else {
    			a.append("struct ");
        		a.append(clazz.underName);
        		a.append("* ");
    		}
    	} else if(clazz == null) {
    		a.append(name);
    	}
		
	}

	/**
     * Read a type usage from a source reader
     * @param contextNode
     * @return
     * @throws EOFException
     * @throws SyntaxError
     */
    public static Type read(SourceContext context, SourceReader reader) throws EOFException {
    	
        reader.skipWhitespace();
        
        boolean resolved = false;
        int mark = reader.mark();
        String type = TypeParser.readTypeName(reader);
        if(type.isEmpty()) {
        
        	reader.reset(mark);
        	type = reader.readName().trim();
        	
        	if(type.isEmpty()) {
                return null;
            } else if(type.equals("This")) {
            	// 'This' is a special case. It resolves to the name of the
        		// class we're in the definition of.
    			ClassDef def = context.getNearest(ClassDef.class);
    			if(def == null) {
    				throw new CompilationFailedError(context.reader.getLocation(), "Using 'This' type outside a class definition!");
    			}
    			type = def.clazz.underName;
    		} else if(type.equals("struct") || type.equals("enum")) {
	        	reader.skipWhitespace();
	        	type += " "+reader.readName().trim();
            }
        	
        } else {
        	resolved = true; // It came straight from TypeParser, so it's a standard type
        }

        reader.skipWhitespace();
        int pointerLevel = reader.readMany("*", " ", true).length();
        int arrayLevel = 0;
        while(reader.matches("[]", true)) {
            arrayLevel++;
            reader.skipWhitespace();
        }
        
        Type typeUsage = new Type(reader.getLocation(),
        		context == null ? null : context.source.getRoot(),
        				type, pointerLevel, arrayLevel);
        typeUsage.isResolved = resolved;
		return typeUsage;
        
    }

    /**
     * 
     * @param clazz
     * @return
     */
    public boolean isInstance(Clazz clazz) {
    	
        return (clazz.isNamed(name) && pointerLevel == 0);
        
    }

    @Override
    public boolean equals(Object o) {
    	
    	if(o == null) {
    		throw new Error("Comparing type "+this+" to null type !");
    	}
    	
    	if(o instanceof Type) {
    		Type typeUsage = (Type) o;
	        return (typeUsage.name.equals(name) && typeUsage.pointerLevel == pointerLevel && typeUsage.arrayLevel == arrayLevel);
    	}
    		
		return super.equals(o);
        
    }

    /**
     * @return a mangled representation of this type, ensuring its uniqueness,
     * whatever its pointer or array level are
     */
    public String toMangledString() {
    	
        StringBuilder sB = new StringBuilder();
        sB.append(name.replace(' ', SEPARATOR_CHAR));
        for(int i = 0; i < pointerLevel; i++) {
            sB.append("__star");
        }
        for(int i = 0; i < arrayLevel; i++) {
            sB.append("__array");
        }
        return sB.toString();
    }
    
    /**
     * @return if this type hasn't been resolved, ie. it's unknown
     */
    public boolean isUnknown() {
    	
    	return (this == UNKNOWN);
    	
    }

    /**
     * @return the pointer level of this type, e.g. 2 for "int**"
     */
	public int getPointerLevel() {
		
		return pointerLevel;
		
	}

	/**
	 * @return the array level of this type, e.g. 2 for "int[][]"
	 */
	public int getArrayLevel() {
		
		return arrayLevel;
		
	}

	/**
	 * @param offset
	 * @return a copy of this type, with an arrayLevel of this arrayLevel + offset
	 */
	public Type deriveArrayLevel(int offset) {

		if(offset < 0 && arrayLevel <= 0) {
			return derivePointerLevel(offset);
		}
		
		Type type = new Type(location, getContext(), name, pointerLevel, arrayLevel + offset);
		type.isResolved = isResolved;
		type.isCover = isCover;
		return type;
		
	}
	
	/**
	 * @param offset
	 * @return a copy of this type, with a pointerLevel of this pointerLevel + offset
	 */
	public Type derivePointerLevel(int offset) {

		Type type = new Type(location, getContext(), name, pointerLevel + offset, arrayLevel);
		type.isResolved = isResolved;
		type.isCover = isCover;
		return type;
		
	}

	/**
	 * safe variant of toString, ie. assemble before to create a representation
	 * @param manager
	 * @return
	 */
	public String toString(AssemblyManager manager) {
		
		manager.queue(this, "Type resolving.");
		this.assemble(manager);
		return toString();
		
	}

	/**
	 * Check if a cast between this type and the argument is valid
	 * @param type the type to test against
	 * @param manager
	 */
	public void checkCast(Type type, AssemblyManager manager) {

		ClassDef myDef = manager.getContext().getClassDef(name);
		ClassDef hisDef = manager.getContext().getClassDef(type.name);
		if(myDef == null || hisDef == null) {
			return; // Not classes, can't compare.
		}
		
		if(!myDef.clazz.isSubclassOf(hisDef.clazz, manager.getContext())) {
			manager.errAndFail("Illegal cast from "+myDef.clazz.fullName+" to "+hisDef.clazz.fullName, this);
		}
		
	}
	
	/**
	 * Overriden because toString() can throw an Exception in case we've not
	 * been resolved yet.
	 */
	@Override
	public String getDescription() {
	
		String desc;
		
		if(clazz != null) {
			try {
				StringBuilder sb = new StringBuilder(clazz.fullName);
				writeStars(sb);
				writeBrackets(sb);
				desc = sb.toString();
			} catch(IOException e) {
				desc = clazz.fullName;
			}
		} else {
			Type.resolveCheckEnabled = false;
			desc = toString();
			Type.resolveCheckEnabled = true;
		}
		
		return desc;
		
	}

	/**
	 * @return the source context of this type usage
	 */
	public SourceContext getSourceContext() {

		if(getContext() == null) {
			return null;
		}
		
		if(getContext().getRoot() == null) {
			return null;
		}
		
		return getContext().getRoot().context;
		
	}

	/**
	 * @return true if this type is a function pointer
	 */
	public boolean isFunctionPointer() {

		// FIXME Oh the hack. Func is a typedef in sdk/OocLib.ooc
		return name.equals("Func");
		
	}

	/**
	 * Return a base type of specified name
	 * @param name
	 * @return
	 */
	public static Type baseType(String name) {

		Type type = new Type(new FileLocation("<"+name+" definition>", -1, -1, -1), null, name);
		type.isResolved = true;
		type.isCover = true;
		return type;
		
	}

	/**
	 * @return true if this type is neither a pointer type nor an array type.
	 */
	public boolean isFlat() {

		return pointerLevel == 0 && arrayLevel == 0;
		
	}
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
	
		// FIXME this is all very cute, but it's pure copy/paste from TypeReference
		// In fact, abominations that are TypeReference and ClassReference should probably
		// disappear
		while(true) {
	    	SyntaxNode next = getNext();
	    	
			if(next instanceof Star) {

				replaceWith(manager, derivePointerLevel(pointerLevel + 1));
	    		next.drop();
	    		
	    	} else if(next instanceof Subscript) {
	    		
	    		Subscript sub = (Subscript) next;
	    		if(sub.nodes.isEmpty()) {
	    			replaceWith(manager, deriveArrayLevel(arrayLevel + 1));	
	    		} else if(sub.getNext() instanceof Name) {
	    			Name name = (Name) sub.getNext();
	    			replaceWith(manager, new ArrayDecl(location, new Variable(this, name.content), sub));
	    			name.drop();
	    		} else {
	    			//FIXME either this should never happen in the user's sourcecode,
	    			//either it's a limitation that should be handled here
	    			manager.err("fixme:'Non-empty subscript after a TypeReference' not followed by a Name.. not supported yet", this);
	    		}
	    		
	    	} else {
	    		
	    		break;
	    		
	    	}
    	}
		
	}

}
