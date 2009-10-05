package org.ooc.backend.cdirty;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ooc.backend.Generator;
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
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VersionBlock;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.model.VersionNodes.VersionAnd;
import org.ooc.frontend.model.VersionNodes.VersionName;
import org.ooc.frontend.model.VersionNodes.VersionNegation;
import org.ooc.frontend.model.VersionNodes.VersionNodeVisitor;
import org.ooc.frontend.model.VersionNodes.VersionOr;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.frontend.parser.TypeArgument;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.structs.MultiMap;
import org.ubi.SourceReader;

public class CGenerator extends Generator implements Visitor {

	public final AwesomeWriter hw;
	public final AwesomeWriter cw;
	public AwesomeWriter current;
	public BuildParams params;

	public CGenerator(File outPath, Module module) throws IOException {
		super(outPath, module);
		String basePath = module.getOutPath();
		File hFile = new File(outPath, basePath + ".h");
		hFile.getParentFile().mkdirs();
		this.hw = new AwesomeWriter(new FileWriter(hFile));
		File cFile = new File(outPath, basePath + ".c");
		this.cw = new AwesomeWriter(new FileWriter(cFile));
		this.current = hw;
	}

	@Override
	public void generate(BuildParams params) throws IOException {
		this.params = params;
		module.accept(this);
		hw.close();
		cw.close();
	}

	public void visit(Module module) throws IOException {
		ModuleWriter.write(module, this);
	}

	public void visit(Add add) throws IOException {
		add.getLeft().accept(this);
		current.app(" + ");
		add.getRight().accept(this);		
	}

	public void visit(Mul mul) throws IOException {
		mul.getLeft().accept(this);
		current.app(" * ");
		mul.getRight().accept(this);		
	}

	public void visit(Sub sub) throws IOException {
		sub.getLeft().accept(this);
		current.app(" - ");
		sub.getRight().accept(this);		
	}

	public void visit(Div div) throws IOException {
		div.getLeft().accept(this);
		current.app(" / ");
		div.getRight().accept(this);
	}

	public void visit(Not not) throws IOException {
		current.app('!');
		not.getExpression().accept(this);		
	}
	
	public void visit(Mod mod) throws IOException {
		mod.getLeft().accept(this);
		current.app(" % ");
		mod.getRight().accept(this);
	}
	
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

	public void visit(FunctionCall functionCall) throws IOException {
		CallWriter.write(functionCall, this);
	}

	public void visit(MemberCall memberCall) throws IOException {
		CallWriter.writeMember(memberCall, this);
	}

	public void visit(Parenthesis parenthesis) throws IOException {
		current.app('(');
		parenthesis.getExpression().accept(this);
		current.app(')');
	}

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

	public void visit(ValuedReturn return1) throws IOException {
		current.app("return ");
		return1.getExpression().accept(this);
	}
	
	public void visit(Return return1) throws IOException {
		current.app("return");
	}

	public void visit(NullLiteral nullLiteral) throws IOException {
		LiteralWriter.writeNull(this);
	}

	public void visit(IntLiteral numberLiteral) throws IOException {
		LiteralWriter.writeInt(numberLiteral, this);
	}
	
	public void visit(FloatLiteral floatLiteral) throws IOException {
		LiteralWriter.writeFloat(floatLiteral, this);
	}

	public void visit(StringLiteral stringLiteral) throws IOException {
		LiteralWriter.writeString(stringLiteral, this);
	}

	public void visit(RangeLiteral rangeLiteral) throws IOException {
		throw new OocCompilationError(rangeLiteral, module,
				"Using a range literal outside a foreach is not supported yet.");
	}

	public void visit(BoolLiteral boolLiteral) throws IOException {
		current.app(boolLiteral.getValue() ? "true" : "false");
	}

	public void visit(CharLiteral charLiteral) throws IOException {
		current.app('\'').app(SourceReader.spelled(charLiteral.getValue())).app('\'');		
	}

	public void visit(Line line) throws IOException {
		current.nl();
		if(line.getStatement() instanceof FunctionCall) CallWriter.bypassPrelude = (FunctionCall) line.getStatement();
		line.getStatement().accept(this);
		if(!(line.getStatement() instanceof ControlStatement || line.getStatement() instanceof VersionBlock)) {
			current.app(';');
		}
	}

	public void visit(Include include) throws IOException {}

	public void visit(If if1) throws IOException {
		ControlStatementWriter.writeIf(if1, this);
	}
	
	public void visit(Else else1) throws IOException {
		ControlStatementWriter.writeElse(else1, this);
	}

	public void visit(While while1) throws IOException {
		ControlStatementWriter.writeWhile(while1, this);
	}

	public void visit(Foreach foreach) throws IOException {
		ControlStatementWriter.writeForeach(foreach, this);
	}

	public void visit(MemberAccess memberAccess) throws IOException {
		AccessWriter.writeMember(memberAccess, this);
	}
	
	public void visit(VariableAccess variableAccess) throws IOException {
		AccessWriter.writeVariable(variableAccess, true, this);
	}

	public void visit(ArrayAccess arrayAccess) throws IOException {
		AccessWriter.writeArray(arrayAccess, this);
	}

	public void visit(VariableDecl variableDecl) throws IOException {
		VariableDeclWriter.write(variableDecl, this);
	}

	public void visit(FunctionDecl functionDecl) throws IOException {
		FunctionDeclWriter.write(functionDecl, this);
	}

	public void visit(ClassDecl classDecl) throws IOException {
		ClassDeclWriter.write(classDecl, this);
	}
	
	public void visit(CoverDecl cover) throws IOException {
		CoverDeclWriter.write(cover, this);
	}
	
	public void visit(TypeArgument typeArgument) throws IOException {
		typeArgument.getType().accept(this);
	}

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

	public void visit(MemberArgument memberArgument) throws IOException {}

	public void visit(MemberAssignArgument memberArgument) throws IOException {}

	public void visit(Type type) throws IOException {
		TypeWriter.write(type, this);
	}

	public void visit(VarArg varArg) throws IOException {
		current.app("...");
	}
	
	public void visit(NodeList<? extends Node> list) throws IOException {
		list.acceptChildren(this);
	}
	
	public void visit(Block block) throws IOException {
		current.openBlock();
		block.acceptChildren(this);
		current.closeBlock();
	}

	public void visit(BuiltinType builtinType) throws IOException {}

	public void visit(VariableDeclAtom variableDeclAtom) throws IOException {}
	
	public void visit(Cast cast) throws IOException {
		CastWriter.write(cast, this);
	}

	public void visit(AddressOf addressOf) throws IOException {
		if(addressOf.getExpression() instanceof VariableAccess) {
			VariableAccess varAcc = (VariableAccess) addressOf.getExpression();
			Type varAccType = varAcc.getRef().getType();
			if(varAccType.getRef() instanceof TypeParam) {
				AccessWriter.write(varAcc, false, this);
				return;
			}
			if(varAccType.getReferenceLevel() == 1) {
				AccessWriter.write(varAcc, false, this, -1);
				return;
			}
		}
		current.app("&(");
		addressOf.getExpression().accept(this);
		current.app(')');
	}

	public void visit(Dereference dereference) throws IOException {
		current.app("(*");
		dereference.getExpression().accept(this);
		current.app(')');
	}

	public void visit(OpDecl opDecl) throws IOException {
		opDecl.getFunc().accept(this);
	}

	public void visit(Import import1) throws IOException {}
	
	public void visit(ArrayLiteral arrayLiteral) throws IOException {
		current.app('{');
		Iterator<Expression> iter = arrayLiteral.getElements().iterator();
		while(iter.hasNext()) {
			Expression element = iter.next();
			boolean doCasting = false;
			if(!element.getType().getName().equals(arrayLiteral.getType().getName())) {
				doCasting = true;
				current.app("((");
				arrayLiteral.getElements().get(0).getType().accept(this);
				current.app(") ");
			}
			element.accept(this);
			if(doCasting) {
				current.app(")");
			}
			if(iter.hasNext()) current.app(", ");
		}
		current.app('}');
	}

	public void visit(Use use) throws IOException {}

	public void visit(BinaryCombination binaryCombination) throws IOException {
		binaryCombination.getLeft().accept(this);
		current.app(' ').app(binaryCombination.getOpString()).app(' ');
		binaryCombination.getRight().accept(this);
	}

	public void visit(MultiMap<?, ?> list) throws IOException {}

	public void visit(FlowControl flow) throws IOException {
		current.app(flow.getKeyword()).app(";");
	}

	public void visit(InterfaceDecl interfaceDecl) throws IOException {
		// huh.. slack off?
	}

	public void visit(Ternary ternary) throws IOException {
		ternary.getCondition().accept(this);
		current.app(" ? ");
		ternary.getValueIfTrue().accept(this);
		current.app(" : ");
		ternary.getValueIfFalse().accept(this);
	}

	public void visit(Match match) throws IOException {
		boolean isFirst = true;
		for(Case case1: match.getCases()) {
			if(!isFirst) {
				current.app(" else ");
			}

			if(case1.getExpr() == null) {
				if(isFirst) current.app(" else ");
			} else {
				if(case1.isFallthrough()) current.app(' ');
				current.app("if (");
				case1.getExpr().accept(this);
				current.app(")");
			}
			
			current.app("{").tab();
			
			for(Line line: case1.getBody()) {
				current.newLine();
				if(line.getStatement() instanceof FunctionCall) {
					CallWriter.bypassPrelude = (FunctionCall) line.getStatement();
				}
				line.getStatement().accept(this);
				current.append(";");
			}
			
			current.untab().nl().app("}");
			if(isFirst) isFirst = false;
		}
	}

	public void visit(Case case1) throws IOException {
		// hmmm... no
	}

	public void visit(VersionBlock versionBlock) throws IOException {
		current.app("\n#if ");
		boolean first = true;
		
		versionBlock.getVersion().acceptChildren(new VersionNodeVisitor() {
			
			public void visit(VersionOr versionOr) {
				// TODO Auto-generated method stub
				
			}
			
			public void visit(VersionAnd versionAnd) {
				// TODO Auto-generated method stub
				
			}
			
			public void visit(VersionNegation versionNegation) {
				// TODO Auto-generated method stub
				
			}
			
			public void visit(VersionName versionName) {
				// TODO Auto-generated method stub
				
			}
		});
		
		visit((Block) versionBlock);
		current.app("\n#endif");
	}

}
