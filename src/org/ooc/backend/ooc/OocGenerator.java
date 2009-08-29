package org.ooc.backend.ooc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.backend.Generator;
import org.ooc.backend.TabbedWriter;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.AddressOf;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.ArrayLiteral;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BinaryCombination;
import org.ooc.frontend.model.Block;
import org.ooc.frontend.model.BoolLiteral;
import org.ooc.frontend.model.BuiltinType;
import org.ooc.frontend.model.Cast;
import org.ooc.frontend.model.CharLiteral;
import org.ooc.frontend.model.ClassDecl;
import org.ooc.frontend.model.Compare;
import org.ooc.frontend.model.ControlStatement;
import org.ooc.frontend.model.CoverDecl;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Else;
import org.ooc.frontend.model.Expression;
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
import org.ooc.frontend.model.Visitable;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.parser.TypeArgument;
import org.ooc.middle.structs.MultiMap;
import org.ubi.SourceReader;

public class OocGenerator extends Generator implements Visitor {

	protected TabbedWriter w;

	public OocGenerator(File outPath, Module module) throws IOException {
		super(outPath, module);
		this.w = new TabbedWriter(new FileWriter(new File(outPath, module.getFileName() + ".gen")));
	}
	
	@Override
	public void generate() throws IOException {
		module.accept(this);
		w.close();
	}
	
	@Override
	public void visit(Module module) throws IOException {
		module.acceptChildren(this);
		w.append('\n'); // traditions make me cry a little bit :)
	}

	@Override
	public void visit(Add add) throws IOException {
		add.getLeft().accept(this);
		w.append(" + ");
		add.getRight().accept(this);
	}

	@Override
	public void visit(Mul mul) throws IOException {
		mul.getLeft().accept(this);
		w.append(" * ");
		mul.getRight().accept(this);
	}

	@Override
	public void visit(Sub sub) throws IOException {
		sub.getLeft().accept(this);
		w.append(" - ");
		sub.getRight().accept(this);
	}

	@Override
	public void visit(Div div) throws IOException {
		div.getLeft().accept(this);
		w.append(" / ");
		div.getRight().accept(this);
	}
	
	@Override
	public void visit(Mod mod) throws IOException {
		mod.getLeft().accept(this);
		w.append(" % ");
		mod.getRight().accept(this);
	}

	@Override
	public void visit(Not not) throws IOException {
		w.append("!");
		not.getExpression().accept(this);
	}

	@Override
	public void visit(FunctionCall call) throws IOException {
		
		w.append(call.getName());
		if(call.getArguments().isEmpty()) return;
		
		w.append('(');
		
		boolean isFirst = true;
		for(Expression arg: call.getArguments()) {
			if(!isFirst) {
				w.append(", ");
			}
			arg.accept(this);
			isFirst = false;
		}
		
		w.append(')');
		
	}
	
	@Override
	public void visit(MemberCall call) throws IOException {
		call.getExpression().accept(this);
		w.append('.');
		visit((FunctionCall) call);
	}
	
	@Override
	public void visit(Instantiation inst) throws IOException {
		w.append("new");
		if(!inst.getName().isEmpty()) {
			w.append(' ');
		}
		visit((FunctionCall) inst);
	}

	@Override
	public void visit(Parenthesis parenthesis) throws IOException {
		w.append('(');
		parenthesis.getExpression().accept(this);
		w.append(')');
	}

	@Override
	public void visit(Assignment assignment) throws IOException {
		assignment.getLeft().accept(this);
		w.append(' ');
		w.append(assignment.getSymbol());
		w.append(' ');
		assignment.getRight().accept(this);
	}

	@Override
	public void visit(ValuedReturn return1) throws IOException {
		w.append("return ");
		return1.getExpression().accept(this);
	}
	
	@Override
	public void visit(Return return1) throws IOException {
		w.append("return");
	}

	@Override
	public void visit(NullLiteral nullLiteral) throws IOException {
		w.append("null");
	}

	@Override
	public void visit(IntLiteral number) throws IOException {
		switch(number.getFormat()) {
		case DEC: 
			w.append(Long.toString(number.getValue())); break;
		case HEX: 
			w.append("0x");
			w.append(Long.toHexString(number.getValue())); break;
		case OCT:
			w.append("0c");
			w.append(Long.toOctalString(number.getValue())); break;
		case BIN: 
			w.append("0b");
			w.append(Long.toBinaryString(number.getValue())); break;
		}
	}

	@Override
	public void visit(StringLiteral stringLiteral) throws IOException {
		w.append('"');
		w.append(SourceReader.spelled(stringLiteral.getValue()));
		w.append('"');
	}

	@Override
	public void visit(RangeLiteral rangeLiteral) throws IOException {
		rangeLiteral.getLower().accept(this);
		w.append("..");
		rangeLiteral.getUpper().accept(this);
	}

	@Override
	public void visit(BoolLiteral boolLiteral) throws IOException {
		w.append(Boolean.toString(boolLiteral.getValue()));
	}

	@Override
	public void visit(CharLiteral charLiteral) throws IOException {
		w.append('\'');
		w.append(SourceReader.spelled(charLiteral.getValue()));
		w.append('\'');
	}

	@Override
	public void visit(Line line) throws IOException {
		w.newLine();
		line.getStatement().accept(this);
		if(!(line.getStatement() instanceof ControlStatement)) {
			w.append(';');
		}
	}

	@Override
	public void visit(Include include) throws IOException {
		w.append("include ");
		w.append(include.getPath());
		w.append(";");
		w.newLine();
	}

	@Override
	public void visit(Import import1) throws IOException {
		w.append("import ");
		w.append(import1.getName());
		w.append(";");
		w.newLine();
	}

	@Override
	public void visit(If if1) throws IOException {
		
		w.append("if (");
		if1.getCondition().accept(this);
		w.append(") ");
		writeControlStatement(if1);
		
	}
	
	@Override
	public void visit(Else else1) throws IOException {
		w.append("else ");
		writeControlStatement(else1);
	}

	@Override
	public void visit(While while1) throws IOException {
		w.append("while (");
		while1.getCondition().accept(this);
		w.append(") ");
		writeControlStatement(while1);
	}

	@Override
	public void visit(Foreach foreach) throws IOException {
		w.append("for (");
		foreach.getVariable().accept(this);
		w.append(": ");
		foreach.getCollection().accept(this);
		w.append(") ");
		writeControlStatement(foreach);
	}
	
	private void writeControlStatement(ControlStatement statement) throws IOException {
		w.append("{");
		w.tab();
		w.newLine();
		for(Line line: statement.getBody()) {
			line.accept(this);
		}
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
	}

	@Override
	public void visit(VariableAccess access) throws IOException {
		w.append(access.getName());
	}

	@Override
	public void visit(ArrayAccess access) throws IOException {
		access.getVariable().accept(this);
		w.append('[');
		access.getIndex().accept(this);
		w.append(']');
	}

	@Override
	public void visit(VariableDecl variableDecl) throws IOException {
		variableDecl.getType().accept(this);
		if(variableDecl.getType().isFlat()) w.append(' ');
		Iterator<VariableDeclAtom> iter = variableDecl.getAtoms().iterator();
		while(iter.hasNext()) {
			VariableDeclAtom atom = iter.next();
			w.append(atom.getName());
			if(atom.getExpression() != null) {
				w.append(" = ");
				atom.getExpression().accept(this);
			}
			if(iter.hasNext()) w.append(", ");
		}
	}

	@Override
	public void visit(VariableDeclAtom variableDeclAtom) throws IOException {
		// do nothing.
	}

	@Override
	public void visit(FunctionDecl node) throws IOException {
	
		w.newLine();
		if(node.isAbstract()) {
			w.append("abstract ");
		}
		w.append("func ");
		w.append(node.getName());
		
		if(!node.getArguments().isEmpty()) {
			w.append('(');
			Iterator<Argument> iter = node.getArguments().iterator();
			while(iter.hasNext()) {
				Argument arg = iter.next();
				arg.accept(this); 
				if(iter.hasNext()) w.append(", ");
			}
			w.append(')');
		}
		
		if(node.getReturnType() != null) {
			w.append(" -> ");
			node.getReturnType().accept(this);
		}
		
		if(node.getBody().isEmpty()) {
		
			w.append(';');
			
		} else {
			
			w.append(" {");
			w.tab();
			
			for(Visitable child: node.getBody()) {
				child.accept(this);
			}
			
			w.untab();
			w.newLine();
			w.append("}");
		
		}
		
	}

	@Override
	public void visit(ClassDecl node) throws IOException {

		w.newLine();
		if(node.getComment() != null) {
			node.getComment().accept(this);
		}
		if(node.isAbstract()) {
			w.append("abstract ");
		}
		w.append("class ");
		w.append(node.getName());
		if(!node.getSuperName().isEmpty()) {
			w.append("from ");
			w.append(node.getSuperName());
		}
		w.append(" {");
		
		if(!node.hasVariables() && !node.hasFunctions()) {
			w.append('}');
			w.newLine();
			w.newLine();
			return;
		}
		
		w.tab();
		w.newLine();
		w.newLine();
		
		for(VariableDecl variable: node.getVariables()) {
			variable.accept(this);
			w.append(';');
			w.newLine();
		}
		
		for(FunctionDecl function: node.getFunctions()) {
			function.accept(this);
			w.newLine();
		}
		
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
		
	}
	
	@Override
	public void visit(CoverDecl cover) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visit(TypeArgument typeArgument) throws IOException {
		typeArgument.getType().accept(this);
	}

	@Override
	public void visit(RegularArgument arg) throws IOException {
		arg.getType().accept(this);
		w.append(' ');
		w.append(arg.getName());
	}

	@Override
	public void visit(MemberArgument arg) throws IOException {
		w.append('=');
		w.append(arg.getName());
	}

	@Override
	public void visit(MemberAssignArgument arg) throws IOException {
		w.append(arg.getName());		
	}
	
	@Override
	public void visit(VarArg varArg) throws IOException {
		w.append("...");
	}

	@Override
	public void visit(Type type) throws IOException {
		w.append(type.getName());
		for(int i = 0; i < type.getPointerLevel(); i++) {
			w.append('*');
		}
	}

	@Override
	public void visit(NodeList<? extends Node> list) throws IOException {
		list.acceptChildren(this);
	}

	@Override
	public void visit(Block block) throws IOException {
		w.append('{');
		w.tab();
		w.newLine();
		block.acceptChildren(this);
		w.untab();
		w.newLine();
		w.append('}');
		w.newLine();
	}

	@Override
	public void visit(BuiltinType builtinType) throws IOException {
		// nothing to do.
	}

	@Override
	public void visit(MemberAccess memberAccess) throws IOException {
		memberAccess.getExpression().accept(this);
		w.append('.');
		visit((VariableAccess) memberAccess);
	}

	@Override
	public void visit(Compare compare) throws IOException {

		compare.getLeft().accept(this);
		switch(compare.getCompareType()) {
			case GREATER: w.append(" > "); break;
			case GREATER_OR_EQUAL: w.append(" >= "); break;
			case LESSER: w.append(" < "); break;
			case LESSER_OR_EQUAL: w.append(" <= "); break;
			case EQUAL: w.append(" == "); break;
			case NOT_EQUAL: w.append(" != "); break;
		}
		compare.getRight().accept(this);
		
	}

	@Override
	public void visit(FloatLiteral floatLiteral) throws IOException {
		w.append(Double.toString(floatLiteral.getValue()));
	}

	@Override
	public void visit(Cast cast) throws IOException {
		cast.getExpression().accept(this);
		w.append(" as ");
		cast.getType().accept(this);
	}

	@Override
	public void visit(AddressOf addressOf) throws IOException {
		addressOf.getExpression().accept(this);
		w.append('&');
	}

	@Override
	public void visit(Dereference dereference) throws IOException {
		dereference.getExpression().accept(this);
		w.append('@');
	}

	@Override
	public void visit(OpDecl opDecl) throws IOException {
		w.append("operator ").append(opDecl.getOpString()).append(" ");
		opDecl.getFunc().accept(this);
	}

	@Override
	public void visit(ArrayLiteral arrayLiteral) throws IOException {
		w.append('[');
		Iterator<Expression> iter = arrayLiteral.getElements().iterator();
		while(iter.hasNext()) {
			iter.next().accept(this);
			if(iter.hasNext()) w.append(", ");
		}
		w.append(']');
	}

	@Override
	public void visit(Use use) throws IOException {
		w.newLine().append("use ").append(use.getIdentifier());
	}

	@Override
	public void visit(BinaryCombination binaryCombination) throws IOException {
		binaryCombination.getLeft().accept(this);
		w.append(' ').append(binaryCombination.getOpString()).append(' ');
		binaryCombination.getRight().accept(this);
	}

	@Override
	public void visit(MultiMap<?, ?> list) throws IOException {}
	
}
