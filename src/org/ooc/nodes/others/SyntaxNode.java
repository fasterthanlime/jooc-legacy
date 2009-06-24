package org.ooc.nodes.others;

import java.io.IOException;

import org.ooc.errors.AssemblyManager;
import org.ooc.features.Feature;
import org.ooc.features.checks.AllContractsImplementedCheck;
import org.ooc.features.checks.NoAbstractFuncInConcreteClassCheck;
import org.ooc.features.core.AccessChainingFeature;
import org.ooc.features.core.ArrayDeclFeature;
import org.ooc.features.core.ArrayLiteralFeature;
import org.ooc.features.core.CascadeCallFeature;
import org.ooc.features.core.InitializationFeature;
import org.ooc.features.core.MultipleVarDeclFeature;
import org.ooc.features.core.PointerInitializationFeature;
import org.ooc.features.core.StaticClassAccessFeature;
import org.ooc.features.core.StaticFunctionCallFeature;
import org.ooc.features.core.StaticReferenceFeature;
import org.ooc.features.core.TypeResolutionFeature;
import org.ooc.nodes.RootNode;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.doc.Comment;
import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.interfaces.LinearNode;
import org.ooc.nodes.interfaces.WriteableToPureC;
import org.ooc.nodes.libs.Include;
import org.ooc.nodes.operators.Arrow;
import org.ooc.nodes.operators.Dot;
import org.ooc.nodes.preprocessor.PreprocessorDirective;
import org.ubi.FileLocation;

/**
 * A node in the Abstract Syntax Tree. Represents e.g. control structures, 
 * class/function definitions
 * @author Amos Wenger
 */
public abstract class SyntaxNode implements WriteableToPureC {
	
	/**
	 * Features are now the preferred way to implement node transformations.
	 * The legacy assembleImpl() way should be migrated progressively
	 * 
	 * Commented features are EXPERIMENTAL and occasionally generate invalid
	 * target code. 
	 */
	public static final Feature[] features = new Feature[] {
		new ArrayDeclFeature(),
		new TypeResolutionFeature(),
		new MultipleVarDeclFeature(),
		new AccessChainingFeature(),
		new PointerInitializationFeature(),
		new ArrayLiteralFeature(),
		new StaticClassAccessFeature(),
		new StaticFunctionCallFeature(),
		new InitializationFeature(),
		new CascadeCallFeature(),
		new StaticReferenceFeature(),
		
		new NoAbstractFuncInConcreteClassCheck(),
		new AllContractsImplementedCheck(),
		//new AssignmentTypeCheck(),
		//new CastTypeCheck(),
		//new MemberCallUnwrapFeature(),
		//new NullCheckFeature(),
	};
	
	/** used to generate nodes' hashes */
	private static int lastHash = 0;
	
	/** true if the node has never ever been assembled. */
    private transient boolean virgin;
    
    /** true if this node shouldn't ever be assembled again */
    private transient boolean locked;
    
    /** This node's parent */
    private SyntaxNodeList parent;
    
    /** This node's context */
    private SyntaxNodeList context;
    
	/** The location of the node, e.g. file name, line number, etc. */
    public final FileLocation location;
    
    /** a unique number, indicates that it was the nth node to be created by the compiler. */
	public transient final int hash;

	/**
	 * pretty much the only thing accepted by C in identifiers, besides alphanumeric
	 * characters it's used to distinguish between package names, class names,
	 * and fields, in generated C identifiers, e.g. "my_package_MyClass_myField"
	 * for field "myField" in class "MyClass" in package "my.package"
	 */
	public static final String SEPARATOR = "_";
	
	/**
	 * the character version of SEPARATOR
	 */
	public static final char SEPARATOR_CHAR = '_';

	/**
	 * Default constructor
	 * @param location
	 */
    public SyntaxNode(final FileLocation location) {
    	if(location == null) {
    		throw new Error("Null location!");
    	}
    	this.hash = lastHash++;
        this.location = location;
        this.virgin = true;
        this.locked = false;
        this.setContext(null);
    }

    @Override
    public String toString() {
    	
    	String result = null;
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            writeToCSource(stringBuilder);
            result = stringBuilder.toString();
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return result;
        
    }
    
    /**
     * @return a nicely formatted description of this node and hints on its content.
     * should be overridden by subclasses to provide more accurate description
     */
    public String getDescription() {
    	return this.getClass().getSimpleName();
    }

    /**
     * Apply transformations to this node, depending on its neighbors and many other
     * things.
     */
    public final void assemble(AssemblyManager manager) {
    	
    	if(virgin) {
    		virgin = false;
    	}
    	
    	if(manager.clean(this)) {
        	assembleImpl(manager);
        }
    	
    	for(Feature feature: features) {
    		feature.apply(manager, this);
    	}
    	
    }
    
    /**
     * Apply transformations to this node, depending on its neighbors and many other
     * things.
     */
    public final void assembleForce(AssemblyManager manager) {
    	
    	if(virgin) {
    		virgin = false;
    	}
    	
    	if(!locked) {
			manager.clean(this);
	    	assembleImpl(manager);
    	}
    	
    	for(Feature feature: features) {
    		feature.apply(manager, this);
    	}
    	
    }

    /**
     * @return true if this node has never, ever, been even looked upon by an
     * assembly manager.
     */
    public final boolean isVirgin() {
        return virgin;
    }
    
    /**
     * put a node in such a state that it will never again be called for assembling
     * by an assembly manager
     */
    public final void lock() {
    	this.locked = true;
    }
    
    protected final void setLocked(boolean assembled) {
        this.locked = assembled;
    }
    
    /**
     * Mark this node as locked, and removes this node from the queue, if it's in it.
     */
    public final void freeze(AssemblyManager manager) {
    	lock();
    	manager.clean(this);
    }
    
    /**
     * @return true if this node should never been assembled again
     */
    public final boolean isLocked() {
    	return locked;
    }

    /**
     * can be overriden by subclasses if they feel the need to modify the syntax
     * tree by themselves before being written.
     * NOTE: this way is quickly on the way to deprecation, as assembleImpl()s are
     * converted to Features.
     * @see org.ooc.features.Feature
     */
    protected void assembleImpl(final AssemblyManager manager) {
    	return;
    }

    protected boolean isSpaced() {
        return true;
    }
    
    protected boolean isIndented() {
        return false;
    }

    private int getIndentLevel() {
        int level = 0;
        SyntaxNode current = this;
        while(current != null) {
            if(current.isIndented()) {
                level++;
            }
            current = current.parent;
        }
        return level;
    }

    public void writeToCHeader(final Appendable a) throws IOException {
    	
        // By default, do nothing in header.
    	
    }
    
    /**
     * Writes this node to C source, as if it was the child of 'fakeParent'
     */
    public void writeToCSourceAsChild(SyntaxNodeList fakeParent, Appendable a) throws IOException {

    	SyntaxNodeList oldParent = parent;
    	this.parent = fakeParent;
    	writeToCSource(a);
    	this.parent = oldParent;
		
	}
    
    /**
     * Write the needed whitespace (e.g. newlines, tabs, spaces, etc.) so that
     * the node is well aligned, thus, the code is well formatted.
     * @param a
     * @throws IOException
     */
    public void writeWhitespace(Appendable a) throws IOException {
    	
    	writeWhitespace(a, 0);
    	
    }
    
    /**
     * Write needed newlines, indentation, and spacing. usually called at the begining
     * of write methods of concrete SyntaxNodes.
     * @param a
     * @param offset
     * @throws IOException 
     */
    public void writeWhitespace(Appendable a, int offset) throws IOException {
    	
    	SyntaxNode prev = getPrev();
    	
    	if(((parent != null && prev == null)
    		|| prev instanceof LineSeparator
    		|| prev instanceof Block)
    		&& !(parent instanceof LinearNode)) {
    		
    		a.append('\n');
    		if(this instanceof FunctionDef) {
    			// Let function definitions breathe !
    			a.append('\n');
    		}
    		
    		writeIndent(a, offset);
    	}
    	
    	if(!isSpaced() || parent == null) {
    		return;
    	}

        if(   prev == null
           || prev instanceof Dot
           || prev instanceof Arrow
           || prev instanceof LineSeparator
           || prev instanceof Block
           || prev instanceof Comment
           || prev instanceof PreprocessorDirective
           || prev instanceof Include
           || prev.getClass() == RawCode.class
        ) {
            // Not adding
        } else {
            a.append(' ');
        }
        
    }
    
    /**
     * write as many tabs as getIndentLevel() to appender a.
     */
    protected void writeIndent(Appendable a) throws IOException {
        writeIndent(a, 0);
    }

    /**
     * write as many tabs as getIndentLevel() + offset to appender a.
     */
    protected void writeIndent(Appendable a, int offset) throws IOException {
        for(int i = getIndentLevel() + offset; i-- > 0;) {
            a.append("\t");
        }
    }

    /**
     * @return a String representation of this node's hierarchy,
     * e.g. parent's types and hashes, nicely formatted.
     */
    public String getHierarchyRepr() {
        SyntaxNode current = this;
        String repr = "";
        while(current != null) {
            repr = current.getClass().getSimpleName() + ":" + current.hash + (repr.isEmpty() ? "" : "->") + repr;
            current = current.parent;
        }
        return repr;
    }

    /**
     * @return the node just before this one, or null if this node has no parent.
     */
	public SyntaxNode getPrev() {
		return parent == null ? null : parent.getPrev(this);
	}
    
	/**
     * @return the node just after this one, or null if this node has no parent.
     */
	public SyntaxNode getNext() {
		return parent == null ? null : parent.getNext(this);
	}
	
	/**
	 * @return the nearest node of type 'type' to the left of this one
	 */
	@SuppressWarnings("unchecked")
	public<T> T getNearestPrevTyped(Class<T> type) {
		SyntaxNode prev = getPrev();
		while(prev != null) {
			if(type.isInstance(prev)) {
				return (T) prev;
			}
			prev = prev.getPrev();
		}
		return null;
	}
	
	/**
	 * @return the nearest node of type 'type' to the right of this one
	 */
	@SuppressWarnings("unchecked")
	public<T> T getNearestNextTyped(Class<T> type) {
		SyntaxNode next = getNext();
		while(next != null) {
			if(type.isInstance(next)) {
				return (T) next;
			}
			next = next.getNext();
		}
		return null;
	}
	
	/**
	 * @return the nearest node which is NOT of type 'type' to the left of this one
	 */
	public SyntaxNode getNearestPrevNotTyped(Class<?> type) {
		SyntaxNode prev = getPrev();
		while(prev != null) {
			if(!type.isInstance(prev)) {
				return prev;
			}
			prev = prev.getPrev();
		}
		return null;
	}
	
	/**
	 * @return the nearest node which is NOT of type 'type' to the right of this one
	 */
	public SyntaxNode getNearestNextNotTyped(Class<?> type) {
		SyntaxNode next = getNext();
		while(next != null) {
			if(!type.isInstance(next)) {
				return next;
			}
			next = next.getNext();
		}
		return null;
	}
	
	/**
     * removes this node from its parent, but does it with panache.
     */
	public void drop() {
		if(parent != null) {
			parent.remove(this);
		} else {
			throw new Error("Trying to detach a "+getClass().getSimpleName()+" but it doesn't have a parent!");
		}
	}
	
	/**
	 * replace this node with 'candidate'. If this node has no parent, does nothing.
	 */
	public void replaceWith(AssemblyManager manager, SyntaxNode candidate) {
		if(parent != null) {
			parent.replace(manager, this, candidate);
		} else {
			if(context != null) {
				System.err.println("(fixme: replaceWith in a no-parent but context node)");
				return;
			}
			throw new Error("Trying to replace a "+getClass().getSimpleName()+" with a "
					+candidate.getClass().getSimpleName()+", but the first doesn't have a parent!");
		}
	}
	
	/**
	 * Add a node just before this one
	 * @param node
	 */
	public void addBefore(SyntaxNode node) {
		if(parent == null) {
			throw new Error("Trying to add a node "+node.getClass().getSimpleName()
					+" before a node "+getClass().getSimpleName()+" without parent.");
		}
		parent.addBefore(this, node);
	}
	
	/**
	 * Add a node just after this one
	 * @param node
	 */
	public void addAfter(SyntaxNode node) {
		if(parent == null) {
			throw new Error("Trying to add a node "+node.getClass().getSimpleName()
					+" after a node "+getClass().getSimpleName()+" without parent.");
		}
		parent.addAfter(this, node);
	}

	/**
	 * moves this node to a new parent, detaching from its existing parent if it has one.
	 */
	public void moveTo(SyntaxNodeList newParent) {
		if(parent != null) {
			drop();
		}
		newParent.add(this);
	}
	
	/**
	 * Adds 'addition' just after the nearest 'LineSeparator' to the left of this node, or
	 * if not found, to the nearest Scope.
	 * @return true if has found a place to add it, false if not (e.g. no LineSeparator and null parent)
	 */
	public boolean addToPrevLineOrScope(SyntaxNode addition) {
		
		LineSeparator separator = this.getNearestPrevTyped(LineSeparator.class);
		if(separator != null) {
			
			separator.getParent().addAfter(separator, addition);
			
		} else if(parent instanceof Scope) {
			
			getParent().addToHead(addition);
			
		} else {
		
			SyntaxNodeList current = this.getParent();
			SyntaxNode beforeWhat = this;
			while(!(current instanceof Scope)) {
				if(current.getParent() != null) {
					beforeWhat = current;
					current = current.getParent();
				} else {
					return false;
				}
			}
			beforeWhat.addToPrevLineOrScope(addition);
			
		}
		return true;
		
	}

	/**
	 * Swap the positions of two nodes
	 * @param other
	 */
	public void swap(SyntaxNode other) {
	
		if(parent != null) {
			int otherIndex = parent.nodes.indexOf(other);
			if(otherIndex != -1) {
				int thisIndex = parent.nodes.indexOf(this);
				parent.nodes.set(thisIndex, other);
				parent.nodes.set(otherIndex, this);
			}
		}
		
	}
	
	/**
	 * @return the parent of this node
	 */
    public SyntaxNodeList getParent() {
    	if(parent == null && context != null) {
    		return context;
    	}
		return parent;
	}
    
    /**
     * @return the root node
     */
    public RootNode getRoot() {
    	
    	if(parent == null) {
    		return null;
    	}
    	
		SyntaxNodeList current = parent;
		while(current.getParent() != null) {
			current = current.getParent();
		}
		
		if(current instanceof RootNode) {
			return (RootNode) current;
		}
		
		return null;
		
	}

    /**
     * Change the parent of this node (ie. move it in the syntax tree)
     * @param parent
     */
	public void setParent(SyntaxNodeList parent) {
		if(this == parent) {
			throw new Error("Attempting to set a node's parent to itself !! we're a "+getDescription()+" and actual parent is a "+getParent());
		}
		if(parent != null && parent.nodes.indexOf(this) == -1) {
			throw new Error("Setting parent to a parent which does not contain this node");
		}
		this.parent = parent;
	}
	
	@Override
	public boolean equals(Object obj) {
	
		if(obj instanceof SyntaxNode) {
			return hash == ((SyntaxNode) obj).hash;
		}
		return false;
		
	}
	
	@Override
	public int hashCode() {
	
		return hash;
		
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(SyntaxNodeList context) {
		this.context = context;
	}

	/**
	 * @return the context
	 */
	public SyntaxNodeList getContext() {
		return context;
	}

}
