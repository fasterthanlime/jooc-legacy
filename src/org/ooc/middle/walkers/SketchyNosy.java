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
import org.ooc.frontend.model.BuiltinType;
import org.ooc.frontend.model.Case;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Else;
import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.FlowControl;
import org.ooc.frontend.model.For;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.InterfaceDecl;
import org.ooc.frontend.model.Line;
import org.ooc.frontend.model.Match;
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
import org.ooc.frontend.model.Ternary;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VersionBlock;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.parser.TypeArgument;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.structs.NodeMap;

/**
 * Whereas Nosy<T> shows where Java abstractions win, SketchyNosy demonstrates
 * where Java's abstraction fail.
 * 
 * A compiler must be *fast*, and Java generics with reflections, dynamic class
 * type test etc, just don't cut it. Get used to it. 
 * 
 * @author Amos Wenger
 */
public class SketchyNosy implements Visitor {

	public final NodeList<Node> stack;
	protected Opportunist<Node> oppo;
	protected boolean running = true;
	
	public static  SketchyNosy get(Opportunist<Node> oppo) {
		return new SketchyNosy(oppo);
	}
	
	public SketchyNosy(Opportunist<Node> oppo) {
		this.stack = new NodeList<Node>(Token.defaultToken);
		this.oppo = oppo;
	}

	public void visitAll(Node node) throws IOException {
		stack.push(node);
		node.acceptChildren(this);
		stack.pop(node);
		
		if(!running) return; // if not running, do nothing
		if(!oppo.take(node, stack)) running = false; // aborted. (D-Nied. Denied).
	}
	
	public SketchyNosy start() {
		running = true;
		return this;
	}
	
	public void visit(Module node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Add node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Mul node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Sub node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;		
	}

	public void visit(Div node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Not node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(FunctionCall node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(MemberCall node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Parenthesis node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Assignment node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(ValuedReturn node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(NullLiteral node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(IntLiteral node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(StringLiteral node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(RangeLiteral node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(BoolLiteral node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(CharLiteral node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Line node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Include node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Import node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(If node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(While node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(For node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}
	
	public void visit(Foreach node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(VariableAccess node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(ArrayAccess node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(VariableDecl node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(VariableDeclAtom node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(FunctionDecl node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(ClassDecl node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(TypeArgument node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(RegularArgument node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(MemberArgument node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(MemberAssignArgument node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}
		
	public void visit(Type node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(VarArg node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(CoverDecl node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(NodeList<? extends Node> node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}
	
	public void visit(Block node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Mod node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Return node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(BuiltinType node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(MemberAccess node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Compare node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(FloatLiteral node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Cast node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(AddressOf node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Dereference node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(OpDecl node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(ArrayLiteral node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Use node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(BinaryCombination node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Else node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(MultiMap<?, ?> node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;		
	}

	public void visit(FlowControl node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(InterfaceDecl node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Ternary node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(Match node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;		
	}

	public void visit(Case node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(VersionBlock node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;
	}

	public void visit(NodeMap<?, ? extends Node> node) throws IOException {
		if(node.hasChildren()) visitAll(node);
		else if(!oppo.take(node, stack)) running = false;		
	}
	
}
