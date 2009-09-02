package org.ooc.middle.hobgoblins;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.IntLiteral.Format;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.middle.Hobgoblin;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.walkers.Nosy;
import org.ooc.middle.walkers.Opportunist;

/**
 * The Checker makes sure everything has been resolved properly. It also makes
 * sure type names are CamelCase and func/vars camelCase
 * 
 * @author Amos Wenger
 */
public class Checker implements Hobgoblin {

	final HashSet<String> funcNames = new HashSet<String>();
	final HashMap<TypeDecl, HashSet<String>> classFuncNames = new HashMap<TypeDecl, HashSet<String>>();
	
	@Override
	public void process(Module module, BuildParams params) throws IOException {
		
		Nosy.get(Node.class, new Opportunist<Node>() {

			@Override
			public boolean take(Node node, NodeList<Node> stack) throws IOException {
				if(node instanceof Type) checkType((Type) node, stack);
				else if(node instanceof FunctionCall) checkFunctionCall((FunctionCall) node, stack);
				else if(node instanceof VariableAccess) checkVariableAccess((VariableAccess) node, stack);
				else if(node instanceof FunctionDecl) checkFunctionDecl((FunctionDecl) node, stack);
				else if(node instanceof VariableDecl) checkVariableDecl((VariableDecl) node, stack);
				else if(node instanceof TypeDecl) checkTypeDecl((TypeDecl) node, stack);
				else if(node instanceof ValuedReturn) checkValuedReturn((ValuedReturn) node, stack);
				return true;
			}
			
			private void checkType(Type node, NodeList<Node> stack) throws IOException {
				if(node.getRef() == null) {
					throw new OocCompilationError(node, stack,
							node.getClass().getSimpleName()+" "+node
							+" hasn't been resolved :(, stack = "+stack);
				}
			}
			
			private void checkFunctionCall(FunctionCall node, NodeList<Node> stack) throws IOException {
				if(node.getImpl() == null) {
					throw new OocCompilationError(node, stack,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :(");
				}
			}
			
			private void checkVariableAccess(VariableAccess node, NodeList<Node> stack) throws IOException {
				if(node.getRef() == null) {
					throw new OocCompilationError(node, stack,
							node.getClass().getSimpleName()+" to "+node.getName()
							+" hasn't been resolved :( Stack = "+stack);
				}
			}
			
			private void checkFunctionDecl(FunctionDecl node, NodeList<Node> stack) throws IOException {
				if(!node.getName().isEmpty()) {
					if(Character.isUpperCase(node.getName().charAt(0)) && !node.isExtern()) {
						throw new OocCompilationError(node, stack,
								"Upper-case function name '"+node.getProtoRepr()
								+"'. Function should always begin with a lowercase letter, e.g. camelCase");
					}
				}
				
				if(!node.isFromPointer()) { 
					String name = node.getName();
					if(node.getTypeDecl() != null) {
						name = node.getTypeDecl().toString() + "." + name;
					}
					
					if(node.isMember()) {
						HashSet<String> set = classFuncNames.get(node.getTypeDecl());
						if(set == null) {
							set = new HashSet<String>();
							classFuncNames.put(node.getTypeDecl(), set);
						}
						if(!set.add(node.getName()+"_"+node.getSuffix())) {
							throwError(node, stack, name);
						}
					} else {
						if(!funcNames.add(node.getName()+"_"+node.getSuffix())) {
							throwError(node, stack, name);
						}
					}
				}
				
				if(!node.getReturnType().isVoid() && !node.isExtern() && !node.isAbstract()) {
					
					if(node.getBody().isEmpty()) {
						if(node.getName().equals("main")) {
							node.getBody().add(new Line(new ValuedReturn(
									new IntLiteral(0, Format.DEC, node.startToken), node.startToken)));
						} else {
							throw new OocCompilationError(node, stack,
									"Returning nothing in function "+node.getProtoRepr()
										+" that should return a "+node.getReturnType());
						}
					}
					
					Line line = node.getBody().getLast();
					if(!(line.getStatement() instanceof Return)) {
						if(node.isEntryPoint()) {
							node.getBody().add(new Line(new ValuedReturn(
									new IntLiteral(0, Format.DEC, node.startToken), node.startToken)));
						} else if(line.getStatement() instanceof Expression) {
							line.setStatement(new ValuedReturn((Expression) line.getStatement(),
									line.getStatement().startToken));
						} else {
							throw new OocCompilationError(node, stack,
									"Returning nothing in function "+node.getProtoRepr()
										+" that should return a "+node.getReturnType());
						}
					}
				}
			}
			
			void throwError(FunctionDecl node, NodeList<Node> stack, String name)
			throws OocCompilationError, EOFException {
				if(name.equals("class") && stack.find(CoverDecl.class) != -1) return;
				throw new OocCompilationError(node, stack,
						"Two functions have the same name '"+name
							+"', add suffix to one of them! e.g. "+name+": func ~suffix "+node.getArgsRepr()+" -> ReturnType");

			}
			
			
			private void checkVariableDecl(VariableDecl node, NodeList<Node> stack) throws EOFException {
				Type varDeclType = node.getType();
				if(varDeclType != null && varDeclType.getRef() != null && !varDeclType.getRef().isExtern()
						&& !varDeclType.getName().isEmpty() && Character.isLowerCase(varDeclType.getName().charAt(0))) {
					throw new OocCompilationError(varDeclType, stack,
							"Variable declaration has type '"+varDeclType.getName()+
							"', which begins with a lowercase letter."+
							" Types should always begin with an uppercase letter, e.g. CamelCase");
				}
				for(VariableDeclAtom atom: node.getAtoms()) {
					if(atom.getName().isEmpty()) continue;
					if(Character.isUpperCase(atom.getName().charAt(0)) && !node.getType().isConst()
							&& node.shouldBeLowerCase()) {
						throw new OocCompilationError(atom, stack,
								"Upper-case variable name '"+atom.getName()+": "+node.getType()
								+"'. Variables should always begin with a lowercase letter, e.g. camelCase");
					}
				}
			}
			
			private void checkTypeDecl(TypeDecl node, NodeList<Node> stack)
				throws OocCompilationError, EOFException {
				if(node.isExtern() || node.getName().isEmpty()) return;
				if(Character.isLowerCase(node.getName().charAt(0))) {
					throw new OocCompilationError(node, stack,
						"Lower-case type name '"+node.getName()
						+"'. Types should always begin with a capital letter, e.g. CamelCase (stack = "+stack);
				
				}
				
				if(!(node instanceof ClassDecl)) return;
				ClassDecl classDecl = (ClassDecl) node;
				
				if(classDecl.isAbstract()) return;
				
				NodeList<FunctionDecl> functions = new NodeList<FunctionDecl>();
				classDecl.getFunctionsRecursive(functions);
				
				for(FunctionDecl decl: functions) {
					FunctionDecl realDecl = classDecl.getFunction(decl.getName(), decl.getSuffix(), null);
					if(realDecl.isAbstract()) {
						throw new OocCompilationError(classDecl, stack, "Class "+classDecl.getName()
								+" must implement "+decl.getProtoRepr()+", or be declared abstract.");
					}
				}
			}
			
			private void checkValuedReturn(ValuedReturn node,
					NodeList<Node> stack) throws EOFException {

				FunctionDecl decl = (FunctionDecl) stack.get(stack.find(FunctionDecl.class));
				if(decl.getReturnType().isVoid()) {
					throw new OocCompilationError(node, stack,
							"Returning a value in function "+decl.getProtoRepr()
								+" which is declared as returning nothing");
				}
				
			}

		}).visit(module);
		
	}

}
