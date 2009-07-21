package org.ooc.nodes.clazz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.doc.Comment;
import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.interfaces.PotentiallyAbstract;
import org.ooc.nodes.keywords.Keyword;
import org.ooc.nodes.others.Initialization;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.SyntaxNodeList;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.structures.Clazz;
import org.ooc.structures.Field;
import org.ooc.structures.Function;
import org.ooc.structures.Member;
import org.ooc.structures.Variable;
import org.ubi.CompilationFailedError;
import org.ubi.FileLocation;

/**
 * A class definition. Contains member variable declarations, member function
 * declarations.
 *
 * @author Amos Wenger
 */
public class ClassDef extends Scope implements PotentiallyAbstract {

	/**
	 * Most of the class' attributes are stored in the Clazz structure.
	 */
    public final Clazz clazz;

	private List<Field> fields;

	/**
	 * The constructor head contains all sorts of thing which should be added
	 * to the beginning of a constructor, such as instance initializers: 
	 * <code>
	 * class MyClass {
	 * 
	 *   int myField = 3; // this is an instance initializer
	 *   
	 *   new() {
	 *     // myField is automatically initialized
	 *   }
	 * 
	 * }
	 * </code>
	 */
	public final SyntaxNodeList constructorHead;
	
	/**
	 * The constructor tail contains all sorts of things which should be added
	 * to the end of a constructor.
	 */
	public final SyntaxNodeList constructorTail;

	private VariableDecl classMember;
	private VariableDecl staticClassMember;
	
	/**
	 * Default constructor
	 * @param location
	 * @param clazz
	 */
    public ClassDef(FileLocation location, Clazz clazz) {
        super(location);
        this.clazz = clazz;
        clazz.setClassDef(this);
        constructorHead = new SyntaxNodeList(location);
        constructorTail = new SyntaxNodeList(location);
        constructorHead.setContext(this);
        constructorTail.setContext(this);
    }

    /**
     * In case of circular references between classes, C requires that there
     * is a forward definition of the class structures. This is it.
     * @param a
     * @throws IOException
     */
    public void writeForwardDef(Appendable a) throws IOException {
	
    	writeWhitespace(a);
        a.append("struct ");
        a.append(this.clazz.underName);
        a.append(";\n");
		
	}
    
    
    @Override
	public void writeToCHeader(Appendable a) throws IOException {
    	
		// Class structure
        
        a.append('\n');
    	writeWhitespace(a);
        a.append("typedef struct ");
        a.append(this.clazz.underNameClass);
        a.append(" {");

        writeClassStructFieldsDeclarations(a);

        a.append("\n}* ");
        a.append(this.clazz.underNameClass);
        a.append(";\n\n");
		
		// Instance structure
		
    	a.append('\n');
    	writeWhitespace(a);
        a.append("typedef struct ");
        a.append(this.clazz.underName);
        a.append(" {");

        writeInstanceStructFieldsDeclarations(a);

        a.append("\n}* ");
        a.append(this.clazz.underName);
        a.append(";\n\n");

        writeStaticFieldsDeclarations(a);
        writeMethodSignatures(a);

    }

	
    @Override
	public void writeToCSource(Appendable a) throws IOException {

		a.append("\n/*\n * Definition of class ");
		a.append(clazz.fullName);
		a.append("\n */");
		
		writeStaticFieldsDefinitions(a);
		
    	ArrayList<FunctionDef> newFuncDefs = new ArrayList<FunctionDef>();
        for(SyntaxNode node: this.nodes) {
        	
            if(node instanceof FunctionDef) {

                FunctionDef funcDef = (FunctionDef) node;
                // constructors are written last.
                if(funcDef.function.getSimpleName().equals("new")) {
                	newFuncDefs.add(funcDef);
                } else {
                	node.writeToCSource(a);
                }

            } else if(node instanceof Comment) {
            	node.writeToCSource(a);
            } else if (node instanceof VariableDecl
            		|| node instanceof LineSeparator
            		|| node instanceof Keyword
            		|| node instanceof Initialization) {
                // Forget it.
            } else {
				throw new CompilationFailedError(node.location, "Unexpected node "
                		+node.getClass().getSimpleName()+" which looks like '"
                		+node.toString().trim()+"', in doubt, no writing");
            }
            
        }
        
        if(!this.clazz.isAbstract) {
            for(FunctionDef newFuncDef: newFuncDefs) {
                newFuncDef.writeToCSource(a); // Write the newFunc last.
            }
        }
            
    }

	
    @Override
	protected void assembleImpl(AssemblyManager manager) {
    	
    	if(!allSupersAssembled(manager)) {
    		manager.queue(this, "Waiting on super-classes to assemble.");
    	}
    	
		manager.queue(constructorHead, "Queuing instance initializations of class "+clazz.fullName);
		manager.queue(constructorTail, "Queuing constructor tail of class "+clazz.fullName);
        if(!assembleAll(manager)) {
        	return;
        }
        
        // Add the class variable
        classMember = new VariableDecl(location, clazz.getClassVariable());
		add(classMember);
        add(new LineSeparator(location));
        
        // Add the static class variable
        staticClassMember = new VariableDecl(location, clazz.getStaticClassVariable());
		add(staticClassMember);
        add(new LineSeparator(location));
    	
    	for(VariableDecl decl: getNodesTyped(VariableDecl.class)) {
            clazz.members.add(new Member(decl, clazz));
        }
    	
        if(!clazz.isAbstract && !clazz.hasUnmangledFunction("new")) {
        	
        	List<Function> zuperNews = clazz.getUnmangledFunctionsRecursive(manager.getContext(), "new");
        	
        	if(zuperNews.isEmpty()) {
        		
				FunctionDef funcDef = new FunctionDef(location, new Function("new",
						clazz.getType(),
						clazz, new TypedArgumentList(location, new ArrayList<Variable>())));
				manager.queue(funcDef, "ClassDef creating an empty new function");
				add(funcDef);
				
			} else {
				
				for(Function zuperNew: zuperNews) {
					FunctionDef movedNew = zuperNew.getFunctionDef(manager.getContext()).copyInClass(clazz);
					movedNew.function.returnType = clazz.getType();
					add(movedNew);
				}
				
			}
        }
        
        fields = new ArrayList<Field>();
        for(Member member: clazz.members) {
        	if(member.getVariableDecl().variable.getName().equals("class")) {
        		fields.add(member);
        		break;
        	}
        }
        
    	List<Clazz> family = getFamily(manager.getContext());
        List<String> functionMangledNames = new ArrayList<String>();
        for(Clazz ancester: family) {
        	
	    	for(Function function: ancester.functions) {
	    		String name = function.getMangledName(null);
				if(!functionMangledNames.contains(name)) {
					fields.add(getImplementation(manager.getContext(),
							function.getSimpleName(), function.args));
					functionMangledNames.add(name);
				}
	        }
	    	
	    	for(Member member: ancester.members) {
	    		// static members are declared only once, or there is a name clash
	    		if(!member.getVariableDecl().variable.getName().equals("class")
	    				&& (ancester == this.clazz || !member.isStatic())) {
	    			fields.add(member);
	    		}
	    	}
	    	
        }
    	
        //lockAndClean(manager);
        
    }

	private List<Clazz> getFamily(SourceContext context) {

		List<Clazz> family = new ArrayList<Clazz>();
		
		Clazz current = clazz;
        while(current != null) {
        	family.add(0, current);
        	current = current.getZuperOrNull(context);
        }
        
        return family;
        
	}
	
	/**on
	 * @param context
	 * @return a list of all function definitions, or null if some functions
	 * haven't been assembled yet
	 */
	public List<FunctionDef> getAllFunctionDefs(SourceContext context) {
		
		List<FunctionDef> functionDefs = new ArrayList<FunctionDef>();
		List<String> functionMangledNames = new ArrayList<String>();
		
        for(Clazz ancester: getFamily(context)) {
        	
	    	for(Function function: ancester.functions) {
	    		String name = function.getMangledName(null);
				if(!functionMangledNames.contains(name)) {
					Function impl = getImplementation(context, function.getSimpleName(), function.args);
					FunctionDef implDef = impl.getFunctionDef(context);
					functionDefs.add(implDef);
					functionMangledNames.add(name);
				}
	        }
	    	
        }
        
        return functionDefs;
		
	}

	/**
	 * Test the assembledness of this class's super classes
	 * @param manager
	 * @return true if all super classes are assembled, false otherwise
	 */
    public boolean allSupersAssembled(AssemblyManager manager) {
    	
    	if(!clazz.hasSuper()) {
    		// No super, soooo.. it's assembled =)
    		return true;
    	}
    	
		Clazz zuperClazz = clazz.getZuperOrNull(manager.getContext());
		if(zuperClazz == null) {
			// Gosh, it's not even in the syntax tree Oo
			return false;
		}
		
		if(manager.isDirtyRecursive(zuperClazz.getClassDef())) {
			// Dirty, we'll come back later.
			return false;
		}
		
		// Alright
		return true;
		
	}
    
    /**
     * Searches for an implementation of a function, by unmangled name
     * @param context
     * @param unmangledFuncName
     * @return
     */
    public FunctionDef getUnmangledFunction(SourceContext context, String unmangledFuncName, boolean recursive) {

        Clazz current = clazz;
        FunctionDef impl = null;
        
        while(current != null) {
        	
        	ClassDef def = current.getClassDef();
            
            List<FunctionDef> funcDefs = def.getNodesTyped(FunctionDef.class);
            for(FunctionDef funcDef: funcDefs) {
                if(funcDef.function.getSimpleName().equals(unmangledFuncName)) {
                    impl = funcDef;
                    break;
                }
            }
            
            if(impl == null && recursive) {
            		current = current.getZuperOrNull(context);
            		continue;
            }
            
            return impl;
        }
        
        return null;

	}
    
	/**
	 * Get a function def for a particular function.
	 * @param func
	 * @return
	 */
    public FunctionDef getFunctionDef(Function func) {

    	List<FunctionDef> funcDefs = getNodesTyped(FunctionDef.class);
        for(FunctionDef funcDef: funcDefs) {
            if(funcDef.function == func) {
                return funcDef;
            }
        }
        return null;

    }

    /**
     * Get all members of this class
     * @param context
     * @return
     */
    public List<VariableDecl> getMembers(SourceContext context) {
        ClassDef current = this;

        ArrayList<VariableDecl> list = new ArrayList<VariableDecl>();
        while(current != null) {
            for(SyntaxNode node: current.getNodesTyped(VariableDecl.class)) {
                list.add((VariableDecl) node);
            }
            current = context.getClassDef(current.clazz.zuperFullName);
        }

        return list;
    }

    /**
     * Search for a member by name.
     * @param context
     * @param name
     * @return the corresponding field, or nul.
     */
    public Variable getMember(SourceContext context, String name) {
        ClassDef current = this;

        while(current != null) {
            for(SyntaxNode node: current.getNodesTyped(VariableDecl.class)) {
                VariableDecl decl = (VariableDecl) node;
                if(decl.variable.getName().equals(name)) {
                    return decl.variable;
                }
            }
            current = context.getClassDef(current.clazz.zuperFullName);
        }

        return null;
    }

    /**
     * Write static fields ("class" fields) definitions, usually in the C source, e.g.
     * <code>
     * type _MyClass_myField = something;
     * </code>
     * or just
     * <code>
     * type _MyClass_myField;
     * </code>
     */
    private void writeStaticFieldsDefinitions(Appendable a) throws IOException  {

    	if(!fields.isEmpty()) {
    		a.append('\n');
    		writeIndent(a);
    	}

    	int count = 0;
        for(Field field : fields) {
        	if(field.isStatic()) {
        		if(field instanceof Member) {
        			Member member = (Member) field;
        			member.writeDefinition(a);
		        	a.append(";");
		        	if(++count < fields.size()) {
			        	a.append('\n');
			        	writeIndent(a);
			        }
        		}
            }
        }
		
	}
    
    /**
     * Write static fields ("class" fields) declarations, usually in the C header, e.g.
     * <code>
     * extern type _MyClass_myField;
     * </code>
     */
    private void writeStaticFieldsDeclarations(Appendable a) throws IOException {

    	if(!fields.isEmpty()) {
    		a.append('\n');
    		writeIndent(a);
    	}

        for(Field field : fields) {
        	if(field.isStatic()) {
        		if(field.writeDeclaration(a, clazz)) {
    				a.append(";\n");
        		}
			}
        }
		
	}

    /**
     * Write structure fields (e.g. "instance" variables and functions)
     */
    private void writeInstanceStructFieldsDeclarations(Appendable a) throws IOException {

    	if(!fields.isEmpty()) {
    		a.append('\n');
    		writeIndent(a, 1);
    	}

    	int count = 0;
        for(Field field : fields) {
        	if(!field.isStatic() && field instanceof Member && field.writeDeclaration(a, clazz)) { 
	        	a.append(";");
	        	if(++count < fields.size()) {
		        	a.append('\n');
		        	writeIndent(a, 1);
		        }
            }
        }

    }
    
    private void writeClassStructFieldsDeclarations(Appendable a) throws IOException {
    	
		// FIXME make it more flexible. (Use a VariableDecl?)
    	a.append('\n');
		writeIndent(a, 1);
		a.append("String name;");
		a.append('\n');
		writeIndent(a, 1);
		a.append("String simpleName;");
    	
    	if(!fields.isEmpty()) {
    		a.append('\n');
    		writeIndent(a, 1);
    	}

    	//int count = 0;
        for(Field field : fields) {
        	if(!field.isStatic() && field instanceof Function && field.writeDeclaration(a, clazz)) { 
	        	a.append(";");
	        	//if(++count < fields.size()) {
		        	a.append('\n');
		        	writeIndent(a, 1);
		        //}
            }
        }
    	
    }
    
    /**
	 * Used for handling "object.class.field" syntax, e.g.
	 * <code>
	 * Array a = new(15);
	 * printf("class of a = %s\n", a.class.name);
	 * </code>
	 * If 'field' is a valid metaclass field, return true.
	 * 
	 * @param field
	 * @return
	 */
	public boolean hasMetaClassField(String field) {
		
		//FIXME make it more flexible.
		if(field.equals("name") || field.equals("simpleName")) {
			return true;
		}
		
		return false;
		
	}

    private void writeMethodSignatures(Appendable a) throws IOException {

        for(Field field: fields) {
        	if(field instanceof Function) {
        		Function fieldFunc = (Function) field;
				if(fieldFunc.writePrototype(a, this.clazz)) {
					a.append(";\n");
				}
        	}
        }

    }

	
    @Override
	protected boolean isIndented() {
        return false;
    }
    
    
    @Override
	protected boolean isSpaced() {
    	return false;
    }

    
    public void setAbstract(boolean isAbstract) {
    	
        clazz.isAbstract = isAbstract;
        
    }
    
    
	public boolean isAbstract() {
    	
		return clazz.isAbstract;
		
	}
    
	
	@Override
	public String getDescription() {
		
		return clazz.fullName;
		
	}
	
	
	@Override
	public String toString() {
		
		return getDescription();
		
	}

	/**
	 * Add an initialization that should be done at instanciation time
	 * @param init
	 */
	public void addInstanceInitialization(SyntaxNode init) {

		constructorHead.add(init);
		
	}
	
	
	@Override
	public Function getImplementation(SourceContext context, String name, TypedArgumentList tal) {

		List<Function> candidates = clazz.getUnmangledFunctionsRecursive(context, name);
    	Function impl = FunctionDef.getImplementation(context, candidates, tal);
    	
    	if(impl == null) {
			// We can't determine the number of arguments of a pointer to function,
			// hence, we don't check
			// TODO add more safety for function pointers
    		Variable member = getMember(context, name);
    		if(member != null) {
    			if(member.type.isFunctionPointer()) {
    				impl = member.newFunctionPointer(location);
    			}
    		}
    	}
		
		if(impl == null) {
			impl = super.getImplementation(context, name, tal);
		}
		
		return impl;
		
    	
    }

}
