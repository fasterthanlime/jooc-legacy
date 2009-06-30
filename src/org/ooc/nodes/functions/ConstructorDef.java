package org.ooc.nodes.functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.AssemblyManager;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.clazz.StaticMemberAccess;
import org.ooc.nodes.control.If;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.operators.Arrow;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.operators.EqualityTest;
import org.ooc.nodes.others.Block;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.NullLiteral;
import org.ooc.nodes.others.RawCode;
import org.ooc.nodes.others.TransparentBlock;
import org.ooc.nodes.text.StringLiteral;
import org.ooc.structures.Clazz;
import org.ooc.structures.Function;
import org.ubi.FileLocation;

/**
 * A constructor definition, e.g. new() in a class
 * 
 * @author Amos Wenger
 */
public class ConstructorDef extends FunctionDef {

	private List<Function> functionsToInit;
	private ClassDef classDef;
	private Block initClassInstance;

	/**
	 * Default constructor
	 * @param location
	 * @param function
	 */
	public ConstructorDef(FileLocation location, Function function) {
		
		super(location, function);
		functionsToInit = new ArrayList<Function>();
		
		initClassInstance = new TransparentBlock(location);
		
		Clazz clazz = function.clazz;
		
		{ If ifCond = new If(location);
			ifCond.add(new StaticMemberAccess(location, clazz, function.clazz.getStaticClassVariable()));
			ifCond.add(new EqualityTest(location));
			ifCond.add(new NullLiteral(location));
		initClassInstance.add(ifCond); }
		
		{ Scope scope = new Scope(location);
			scope.add(new StaticMemberAccess(location, clazz, function.clazz.getStaticClassVariable()));
			scope.add(new Assignment(location));
			{ FunctionCall malloc = new FunctionCall(location, "GC_malloc"); // FIXME what if we want to turn the GC off?
				{ FunctionCall sizeof = new FunctionCall(location, "sizeof");
					sizeof.add(new RawCode(location, "struct "+clazz.underNameClass));
				malloc.add(sizeof); }
			scope.add(malloc);
			scope.add(new LineSeparator(location));
			
			/** Reflection: name field */
			scope.add(new StaticMemberAccess(location, clazz, function.clazz.getStaticClassVariable()));
			scope.add(new Arrow(location));
			scope.add(new RawCode(location, "name "));
			scope.add(new Assignment(location));
			scope.add(new StringLiteral(location, function.clazz.fullName));
			scope.add(new LineSeparator(location));
			
			/** Reflection: fullName field */
			scope.add(new StaticMemberAccess(location, clazz, function.clazz.getStaticClassVariable()));
			scope.add(new Arrow(location));
			scope.add(new RawCode(location, "simpleName "));
			scope.add(new Assignment(location));
			scope.add(new StringLiteral(location, function.clazz.simpleName));
			scope.add(new LineSeparator(location));
			
		initClassInstance.add(scope); }
			
		}
		
		initClassInstance.add(new MemberAccess(location, function.clazz.getThis(), function.clazz.getClassVariable()));
		initClassInstance.add(new Assignment(location));
		initClassInstance.add(new StaticMemberAccess(location, clazz, function.clazz.getStaticClassVariable()));
		initClassInstance.add(new LineSeparator(location));
		
	}
	
	
	@Override
	public void writeToCSource(Appendable a) throws IOException {
		
		if(function.isAbstract) {
			System.out.println("We're abstract, returning !");
            return;
        }

        if(comment != null) {
			comment.writeToCSource(a);
		} else {
			writeWhitespace(a, -1);
		}
        writePrototype(a, false); // There's no 'this' argument in a Constructor
        a.append(" {\n\n");

        writeIndent(a);
        a.append(function.clazz.underName);
        a.append(" this = GC_malloc(sizeof(struct ");
        a.append(function.clazz.underName);
        a.append("));\n");
        
        classDef.constructorHead.writeToCSourceAsChild(this, a);
        
        initClassInstance.writeToCSourceAsChild(this, a);
        
        a.append("\n\n");
        for(Function func: functionsToInit) {
        	writeIndent(a);
            writePointerAssignment(a, func, function.clazz);
            a.append('\n');
        }
        
        classDef.constructorTail.writeToCSourceAsChild(this, a);
        
        writeBody(a);
        
    	writeWhitespace(a);
        a.append("return this;");
        
        writeWhitespace(a, -1);
        a.append("\n}");
		
	}
	
	/**
     * Write to 'a' the assignment to the function pointer used in C code generation.
     */
    private void writePointerAssignment(Appendable a, Function function, Clazz destClazz) throws IOException {
    	
    	if(function.isStatic) {
    		return; // Don't write assignment for static functions
    	}
    	
        a.append("this->class->");
        a.append(function.getMangledName(null));
        a.append(" = (");
        
        /* function pointer starts */
        function.returnType.writeToCSource(a);
    	a.append(" (*)(");
    	function.writeArgs(a, destClazz);
        a.append(')');
        /* function pointer ends */
        
        a.append(") &");
        a.append(function.getMangledName());
        a.append(";");
        
    }
	
	
	@Override
	protected void assembleImpl(AssemblyManager manager) {
		
		super.assembleImpl(manager);
		
		classDef = function.clazz.getClassDef();
		if(!classDef.allSupersAssembled(manager)) {
			manager.queue(this, "Waiting on super-classes to assemble..");
			return;
		}
		
        List<FunctionDef> list = classDef.getNodesTyped(FunctionDef.class);
        
        // Check that all are assembled
		for(FunctionDef funcDef: list) {
			if(funcDef == null) {
				manager.queue(this, "Waiting on class to assemble all functions..");
				return;
			}
            if(funcDef != this && manager.isDirty(funcDef)) {
            	if(!(funcDef instanceof ConstructorDef)
            	|| ((ConstructorDef) funcDef).hasCallTo(manager.getContext(), this)) {
            		
            		manager.queue(this, "Waiting on function "+funcDef.function.getSimplePrototype()+" to assemble.");
            		return;
            		
            	}
            }
        }
		
		List<FunctionDef> contractList = classDef.getAllFunctionDefs(manager.getContext());
		for(FunctionDef funcDef: contractList) {
			if(!funcDef.function.isConstructor()) {
				functionsToInit.add(funcDef.function);
			}
		}
		
	}

	private boolean hasCallTo(SourceContext context, ConstructorDef def) {
		
		ClassDef classDef = getNearest(ClassDef.class);
		List<ConstructorDef> constructors = classDef.getNodesTyped(ConstructorDef.class);
		List<Function> candidates = new ArrayList<Function>();
		for(ConstructorDef constructor: constructors) {
			candidates.add(constructor.function);
		}
		
		for(FunctionCall call: getNodesTyped(FunctionCall.class, true)) {
			if(call.name.equals("this") || call.name.equals("super")) {
				Function impl = FunctionDef.getImplementation(context, candidates,
						new TypedArgumentList(call));
				if(impl == def.function) {
					return true;
				}
			}
		}
		
		return false;
		
	}

}
