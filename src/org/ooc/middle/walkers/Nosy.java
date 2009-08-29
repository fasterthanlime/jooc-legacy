package org.ooc.middle.walkers;

import java.io.IOException;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.AddressOf;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.ArrayLiteral;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BinaryCombination;
import org.ooc.frontend.model.Block;
import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.FlowControl;
import org.ooc.frontend.model.BuiltinType;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Else;
import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.Instantiation;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.MemberAccess;
import org.ooc.frontend.model.MemberArgument;
import org.ooc.frontend.model.MemberAssignArgument;
import org.ooc.frontend.model.MemberCall;
import org.ooc.frontend.model.Mod;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.Mul;
import org.ooc.frontend.model.Node;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Not;
import org.ooc.frontend.model.NullLiteral;
import org.ooc.frontend.model.OpDecl;
import org.ooc.frontend.model.Parenthesis;
import org.ooc.frontend.model.RangeLiteral;
import org.ooc.frontend.model.RegularArgument;
import org.ooc.frontend.model.Return;
import org.ooc.frontend.model.StringLiteral;
import org.ooc.frontend.model.Sub;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.parser.TypeArgument;
import org.ooc.middle.structs.MultiMap;

public class Nosy<T> implements Visitor {

	public final NodeList<Node> stack;
	protected Class<T> clazz;
	protected Opportunist<T> oppo;
	protected boolean running = true;
	
	public static <T> Nosy<T> get(Class<T> clazz, Opportunist<T> oppo) {
		return new Nosy<T>(clazz, oppo);
	}
	
	public Nosy(Class<T> clazz, Opportunist<T> oppo) {
		this.stack = new NodeList<Node>(Token.defaultToken);
		this.clazz = clazz;
		this.oppo = oppo;
	}

	public void visit(Node node) throws IOException {
		if(!running) return; // if not running, do nothing

		if(node.hasChildren()) {
			stack.push(node);
			node.acceptChildren(this);
			stack.pop();
		}
		
		if(!running) return; // if not running, do nothing
		
		if(clazz.isInstance(node)) {
			if(!oppo.take(clazz.cast(node), stack)) {
				running = false; // aborted. (D-Nied. Denied).
			}
		}
	}
	
	public Nosy<T> start() {
		running = true;
		return this;
	}
	
	@Override
	public void visit(Module module) throws IOException {
		visit((Node) module);
	}

	@Override
	public void visit(Add add) throws IOException {
		visit((Node) add);
	}

	@Override
	public void visit(Mul mul) throws IOException {
		visit((Node) mul);
	}

	@Override
	public void visit(Sub sub) throws IOException {
		visit((Node) sub);		
	}

	@Override
	public void visit(Div div) throws IOException {
		visit((Node) div);
	}

	@Override
	public void visit(Not not) throws IOException {
		visit((Node) not);		
	}

	@Override
	public void visit(FunctionCall functionCall) throws IOException {
		visit((Node) functionCall);	
	}

	@Override
	public void visit(MemberCall memberCall) throws IOException {
		visit((Node) memberCall);	
	}

	@Override
	public void visit(Instantiation inst) throws IOException {
		visit((Node) inst);	
	}

	@Override
	public void visit(Parenthesis parenthesis) throws IOException {
		visit((Node) parenthesis);	
	}

	@Override
	public void visit(Assignment assignment) throws IOException {
		visit((Node) assignment);		
	}

	@Override
	public void visit(ValuedReturn return1) throws IOException {
		visit((Node) return1);		
	}

	@Override
	public void visit(NullLiteral nullLiteral) throws IOException {
		visit((Node) nullLiteral);		
	}

	@Override
	public void visit(IntLiteral numberLiteral) throws IOException {
		visit((Node) numberLiteral);
	}

	@Override
	public void visit(StringLiteral stringLiteral) throws IOException {
		visit((Node) stringLiteral);
	}

	@Override
	public void visit(RangeLiteral rangeLiteral) throws IOException {
		visit((Node) rangeLiteral);		
	}

	@Override
	public void visit(BoolLiteral boolLiteral) throws IOException {
		visit((Node) boolLiteral);		
	}

	@Override
	public void visit(CharLiteral charLiteral) throws IOException {
		visit((Node) charLiteral);		
	}

	@Override
	public void visit(Line line) throws IOException {
		visit((Node) line);		
	}

	@Override
	public void visit(Include include) throws IOException {
		visit((Node) include);
	}

	@Override
	public void visit(Import import1) throws IOException {
		visit((Node) import1);		
	}

	@Override
	public void visit(If if1) throws IOException {
		visit((Node) if1);		
	}

	@Override
	public void visit(While while1) throws IOException {
		visit((Node) while1);		
	}

	@Override
	public void visit(Foreach foreach) throws IOException {
		visit((Node) foreach);		
	}

	@Override
	public void visit(VariableAccess variableAccess) throws IOException {
		visit((Node) variableAccess);
	}

	@Override
	public void visit(ArrayAccess arrayAccess) throws IOException {
		visit((Node) arrayAccess);
	}

	@Override
	public void visit(VariableDecl variableDecl) throws IOException {
		visit((Node) variableDecl);	
	}

	@Override
	public void visit(VariableDeclAtom atom) throws IOException {
		visit((Node) atom);
	}

	@Override
	public void visit(FunctionDecl functionDecl) throws IOException {
		visit((Node) functionDecl);	
	}

	@Override
	public void visit(ClassDecl classDecl) throws IOException {
		visit((Node) classDecl);		
	}

	@Override
	public void visit(TypeArgument typeArgument) throws IOException {
		visit((Node) typeArgument);	
	}

	@Override
	public void visit(RegularArgument regularArgument) throws IOException {
		visit((Node) regularArgument);		
	}

	@Override
	public void visit(MemberArgument memberArgument) throws IOException {
		visit((Node) memberArgument);	
	}

	@Override
	public void visit(MemberAssignArgument memberArgument) throws IOException {
		visit((Node) memberArgument);		
	}

	@Override
	public void visit(Type type) throws IOException {
		visit((Node) type);		
	}

	@Override
	public void visit(VarArg varArg) throws IOException {
		visit((Node) varArg);		
	}

	@Override
	public void visit(CoverDecl cover) throws IOException {
		visit((Node) cover);		
	}

	@Override
	public void visit(NodeList<? extends Node> list) throws IOException {
		visit((Node) list);
	}
	
	@Override
	public void visit(Block block) throws IOException {
		visit((Node) block);
	}

	@Override
	public void visit(Mod mod) throws IOException {
		visit((Node) mod);
	}

	@Override
	public void visit(Return return1) throws IOException {
		visit((Node) return1);
	}

	@Override
	public void visit(BuiltinType builtinType) throws IOException {
		visit((Node) builtinType);
	}

	@Override
	public void visit(MemberAccess memberAccess) throws IOException {
		visit((Node) memberAccess);
	}

	@Override
	public void visit(Compare compare) throws IOException {
		visit((Node) compare);
	}

	@Override
	public void visit(FloatLiteral floatLiteral) throws IOException {
		visit((Node) floatLiteral);
	}

	@Override
	public void visit(Cast cast) throws IOException {
		visit((Node) cast);
	}

	@Override
	public void visit(AddressOf addressOf) throws IOException {
		visit((Node) addressOf);
	}

	@Override
	public void visit(Dereference dereference) throws IOException {
		visit((Node) dereference);
	}

	@Override
	public void visit(OpDecl opDecl) throws IOException {
		visit((Node) opDecl);
	}

	@Override
	public void visit(ArrayLiteral arrayLiteral) throws IOException {
		visit((Node) arrayLiteral);
	}

	@Override
	public void visit(Use use) throws IOException {
		visit((Node) use);
	}

	@Override
	public void visit(BinaryCombination binaryCombination) throws IOException {
		visit((Node) binaryCombination);
	}

	@Override
	public void visit(Else else1) throws IOException {
		visit((Node) else1);
	}

	@Override
	public void visit(MultiMap<?, ?> list) throws IOException {
		visit((Node) list);
	}

	@Override
	public void visit(FlowControl break1) throws IOException {
		visit((Node) break1);
	}
	
}
