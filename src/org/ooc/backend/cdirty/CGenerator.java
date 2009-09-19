package org.ooc.backend.cdirty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.backend.Generator;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.AddressOf;
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
import org.ooc.frontend.model.FlowControl;
import org.ooc.frontend.model.Foreach;
import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.FunctionCall;
import org.ooc.frontend.model.FunctionDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.If;
import org.ooc.frontend.model.Import;
import org.ooc.frontend.model.Include;
import org.ooc.frontend.model.IntLiteral;
import org.ooc.frontend.model.InterfaceDecl;
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
import org.ooc.frontend.parser.BuildParams;
import org.ooc.frontend.parser.TypeArgument;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.structs.MultiMap;
import org.ubi.SourceReader;

public class CGenerator extends Generator implements Visitor {

	public final AwesomeWriter hw;
	public final AwesomeWriter cw;
	public AwesomeWriter current;

	public CGenerator(File outPath, Module module) throws IOException {
		super(outPath, module);
		String basePath = module.getFullName().replace('.', File.separatorChar);
		File hFile = new File(outPath, basePath + ".h");
		hFile.getParentFile().mkdirs();
		this.hw = new AwesomeWriter(new FileWriter(hFile));
		File cFile = new File(outPath, basePath + ".c");
		this.cw = new AwesomeWriter(new FileWriter(cFile));
		this.current = hw;
	}

	@Override
	public void generate(BuildParams params) throws IOException {
		module.accept(this);
		hw.close();
		cw.close();
	}

	@Override
	public void visit(Module module) throws IOException {
		ModuleWriter.write(module, this);
	}

	@Override
	public void visit(Add add) throws IOException {
		add.getLeft().accept(this);
		current.app(" + ");
		add.getRight().accept(this);		
	}

	@Override
	public void visit(Mul mul) throws IOException {
		mul.getLeft().accept(this);
		current.app(" * ");
		mul.getRight().accept(this);		
	}

	@Override
	public void visit(Sub sub) throws IOException {
		sub.getLeft().accept(this);
		current.app(" - ");
		sub.getRight().accept(this);		
	}

	@Override
	public void visit(Div div) throws IOException {
		div.getLeft().accept(this);
		current.app(" / ");
		div.getRight().accept(this);
	}

	@Override
	public void visit(Not not) throws IOException {
		current.app('!');
		not.getExpression().accept(this);		
	}
	
	@Override
	public void visit(Mod mod) throws IOException {
		mod.getLeft().accept(this);
		current.app(" % ");
		mod.getRight().accept(this);
	}
	
	@Override
	public void visit(Compare compare) throws IOException {
		compare.getLeft().accept(this);
		switch(compare.getCompareType()) {
			case GREATER: current.app(" > "); break;
			case GREATER_OR_EQUAL: current.app(" >= "); break;
			case LESSER: current.app(" < "); break;
			case LESSER_OR_EQUAL: current.app(" <= "); break;
			case EQUAL: current.app(" == "); break;
			case NOT_EQUAL: current.app(" != "); break;
		}
		compare.getRight().accept(this);
	}

	@Override
	public void visit(FunctionCall functionCall) throws IOException {
		CallWriter.write(functionCall, this);
	}

	@Override
	public void visit(MemberCall memberCall) throws IOException {
		CallWriter.writeMember(memberCall, this);
	}

	@Override
	public void visit(Parenthesis parenthesis) throws IOException {
		current.app('(');
		parenthesis.getExpression().accept(this);
		current.app(')');
	}

	@Override
	public void visit(Assignment assignment) throws IOException {
		Expression left = assignment.getLeft();
		if(left instanceof VariableAccess) {
			AccessWriter.write((VariableAccess) left, false, this);
		} else {
			left.accept(this);
		}
		current.app(' ').app(assignment.getSymbol()).app(' ');
		assignment.getRight().accept(this);
	}

	@Override
	public void visit(ValuedReturn return1) throws IOException {
		current.app("return ");
		return1.getExpression().accept(this);
	}
	
	@Override
	public void visit(Return return1) throws IOException {
		current.app("return");
	}

	@Override
	public void visit(NullLiteral nullLiteral) throws IOException {
		LiteralWriter.writeNull(this);
	}

	@Override
	public void visit(IntLiteral numberLiteral) throws IOException {
		LiteralWriter.writeInt(numberLiteral, this);
	}
	
	@Override
	public void visit(FloatLiteral floatLiteral) throws IOException {
		LiteralWriter.writeFloat(floatLiteral, this);
	}

	@Override
	public void visit(StringLiteral stringLiteral) throws IOException {
		LiteralWriter.writeString(stringLiteral, this);
	}

	@Override
	public void visit(RangeLiteral rangeLiteral) throws IOException {
		throw new OocCompilationError(rangeLiteral, module,
				"Using a range literal outside a foreach is not supported yet.");
	}

	@Override
	public void visit(BoolLiteral boolLiteral) throws IOException {
		current.app(boolLiteral.getValue() ? "true" : "false");
	}

	@Override
	public void visit(CharLiteral charLiteral) throws IOException {
		current.app('\'').app(SourceReader.spelled(charLiteral.getValue())).app('\'');		
	}

	@Override
	public void visit(Line line) throws IOException {
		current.nl();
		if(line.getStatement() instanceof FunctionCall) CallWriter.noCast = (FunctionCall) line.getStatement();
		line.getStatement().accept(this);
		if(!(line.getStatement() instanceof ControlStatement)) {
			current.app(';');
		}
	}

	@Override
	public void visit(Include include) throws IOException {}

	@Override
	public void visit(If if1) throws IOException {
		ControlStatementWriter.writeIf(if1, this);
	}
	
	@Override
	public void visit(Else else1) throws IOException {
		ControlStatementWriter.writeElse(else1, this);
	}

	@Override
	public void visit(While while1) throws IOException {
		ControlStatementWriter.writeWhile(while1, this);
	}

	@Override
	public void visit(Foreach foreach) throws IOException {
		ControlStatementWriter.writeForeach(foreach, this);
	}

	@Override
	public void visit(MemberAccess memberAccess) throws IOException {
		AccessWriter.writeMember(memberAccess, this);
	}
	
	@Override
	public void visit(VariableAccess variableAccess) throws IOException {
		AccessWriter.writeVariable(variableAccess, true, this);
	}

	@Override
	public void visit(ArrayAccess arrayAccess) throws IOException {
		AccessWriter.writeArray(arrayAccess, this);
	}

	@Override
	public void visit(VariableDecl variableDecl) throws IOException {
		VariableDeclWriter.write(variableDecl, this);
	}

	@Override
	public void visit(FunctionDecl functionDecl) throws IOException {
		FunctionDeclWriter.write(functionDecl, this);
	}

	@Override 
	public void visit(ClassDecl classDecl) throws IOException {
		ClassDeclWriter.write(classDecl, this);
	}
	
	@Override
	public void visit(CoverDecl cover) throws IOException {
		CoverDeclWriter.write(cover, this);
	}
	
	@Override
	public void visit(TypeArgument typeArgument) throws IOException {
		typeArgument.getType().accept(this);
	}

	@Override
	public void visit(RegularArgument regularArgument) throws IOException {
		Type type = regularArgument.getType();
		if(type.isArray()) {
			current.app(type.getName()).app(' ').app(regularArgument.getName());
			for(int i = 0; i < type.getPointerLevel(); i++) {
				current.app("[]");
			}
		} else {
			if(type instanceof FuncType) {
				TypeWriter.writeFuncPointer((FunctionDecl) type.getRef(), regularArgument.getName(), this);
			} else {
				TypeWriter.writeSpaced(type, this);
				current.app(regularArgument.getName());
			}
		}
	}

	@Override
	public void visit(MemberArgument memberArgument) throws IOException {}

	@Override
	public void visit(MemberAssignArgument memberArgument) throws IOException {}

	@Override
	public void visit(Type type) throws IOException {
		TypeWriter.write(type, this);
	}

	@Override
	public void visit(VarArg varArg) throws IOException {
		current.app("...");
	}
	
	@Override
	public void visit(NodeList<? extends Node> list) throws IOException {
		list.acceptChildren(this);
	}
	
	@Override
	public void visit(Block block) throws IOException {
		current.openBlock();
		block.acceptChildren(this);
		current.closeBlock();
	}

	@Override
	public void visit(BuiltinType builtinType) throws IOException {}

	@Override
	public void visit(VariableDeclAtom variableDeclAtom) throws IOException {}
	
	@Override
	public void visit(Cast cast) throws IOException {
		CastWriter.write(cast, this);
	}

	@Override
	public void visit(AddressOf addressOf) throws IOException {
		if(addressOf.getExpression() instanceof VariableAccess) {
			VariableAccess varAcc = (VariableAccess) addressOf.getExpression();
			if(varAcc.getRef().getType().getRef() instanceof TypeParam) {
				AccessWriter.write(varAcc, false, this);
				return;
			}
		}
		current.app("&(");
		addressOf.getExpression().accept(this);
		current.app(')');
	}

	@Override
	public void visit(Dereference dereference) throws IOException {
		current.app("(*");
		dereference.getExpression().accept(this);
		current.app(')');
	}

	@Override
	public void visit(OpDecl opDecl) throws IOException {
		opDecl.getFunc().accept(this);
	}

	@Override
	public void visit(Import import1) throws IOException {}
	
	@Override
	public void visit(ArrayLiteral arrayLiteral) throws IOException {
		current.app('{');
		Iterator<Expression> iter = arrayLiteral.getElements().iterator();
		while(iter.hasNext()) {
			iter.next().accept(this);
			if(iter.hasNext()) current.app(", ");
		}
		current.app('}');
	}

	@Override
	public void visit(Use use) throws IOException {}

	@Override
	public void visit(BinaryCombination binaryCombination) throws IOException {
		binaryCombination.getLeft().accept(this);
		current.app(' ').app(binaryCombination.getOpString()).app(' ');
		binaryCombination.getRight().accept(this);
	}

	@Override
	public void visit(MultiMap<?, ?> list) throws IOException {}

	@Override
	public void visit(FlowControl flow) throws IOException {
		current.app(flow.getKeyword()).app(";");
	}

	@Override
	public void visit(InterfaceDecl interfaceDecl) throws IOException {
		// huh.. slack off?
	}

}
