package org.ooc.backend.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.ooc.backend.Generator;
import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.Add;
import org.ooc.frontend.model.AddressOf;
import org.ooc.frontend.model.Argument;
import org.ooc.frontend.model.ArrayAccess;
import org.ooc.frontend.model.ArrayLiteral;
import org.ooc.frontend.model.Assignment;
import org.ooc.frontend.model.BinaryCombination;
import org.ooc.frontend.model.BinaryNegation;
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
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Dereference;
import org.ooc.frontend.model.Div;
import org.ooc.frontend.model.Else;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FloatLiteral;
import org.ooc.frontend.model.FlowControl;
import org.ooc.frontend.model.For;
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
import org.ooc.frontend.model.TypeDecl;
import org.ooc.frontend.model.TypeParam;
import org.ooc.frontend.model.Use;
import org.ooc.frontend.model.ValuedReturn;
import org.ooc.frontend.model.VarArg;
import org.ooc.frontend.model.VariableAccess;
import org.ooc.frontend.model.VariableDecl;
import org.ooc.frontend.model.VersionBlock;
import org.ooc.frontend.model.While;
import org.ooc.frontend.model.VariableDecl.VariableDeclAtom;
import org.ooc.frontend.parser.BuildParams;
import org.ooc.frontend.parser.TypeArgument;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.structs.MultiMap;
import org.ooc.middle.structs.NodeMap;
import org.ubi.SourceReader;

public class JSONGenerator extends Generator implements Visitor {

	public File out;
	public JSONArray root;

	public JSONGenerator(File outPath, Module module) {
		super(outPath, module);
		String basePath = module.getOutPath();
		
		this.root = new JSONArray();
		this.out = new File(outPath, basePath + ".json");
		this.out.getParentFile().mkdirs();
	}

	@Override
	public void generate(BuildParams params) throws IOException {
		module.accept(this);
		FileWriter writer = new FileWriter(this.out);
		try {
			root.write(writer);
		} catch(JSONException e) {
			throw new IOException("Failed.");
		}
		writer.close();
	}

	public void visit(Module module) throws IOException {
		/* the "!module" entity */
		try {
			JSONObject obj = new JSONObject();
			obj.put("type", "module");
			obj.put("tag", "!module");
			obj.put("path", module.getFullName().replace('.', '/')); // we want the ooc name.
			JSONArray imports = new JSONArray();
			for(Import import_: module.getImports()) {
				imports.put(import_.getPath());
			}
			obj.put("imports", imports);
			addObject(obj);
		} catch (JSONException e) {
			throw new IOException("Failed.");
		}
		/* functions */
		NodeList<FunctionDecl> functions = new NodeList<FunctionDecl>();
		module.getFunctions(functions);
		functions.accept(this);
		/* classes */
		for(TypeDecl type: module.getTypes().values()) {
			if(type instanceof ClassDecl || type instanceof CoverDecl)
				type.accept(this);
		}
		/* variables */
		NodeList<VariableDecl> variables = new NodeList<VariableDecl>();
		module.getVariables(variables);
		variables.accept(this);
	};
	
	public void visit(Add add) throws IOException {};
	public void visit(Mul mul) throws IOException {};
	public void visit(Sub sub) throws IOException {};
	public void visit(Div div) throws IOException {};
	public void visit(Not not) throws IOException {};
	public void visit(BinaryNegation binaryNegation) throws IOException {};
	public void visit(Mod mod) throws IOException {};
	public void visit(Compare compare) throws IOException {};
	
	public void visit(FunctionCall functionCall) throws IOException {};
	public void visit(MemberCall memberCall) throws IOException {};
	
	public void visit(Parenthesis parenthesis) throws IOException {};
	public void visit(Assignment assignment) throws IOException {};
	public void visit(ValuedReturn return1) throws IOException {};
	public void visit(Return return1) throws IOException {};
	
	public void visit(NullLiteral nullLiteral) throws IOException {};
	public void visit(IntLiteral numberLiteral) throws IOException {};
	public void visit(FloatLiteral floatLiteral) throws IOException {};
	public void visit(StringLiteral stringLiteral) throws IOException {};
	public void visit(RangeLiteral rangeLiteral) throws IOException {};
	public void visit(BoolLiteral boolLiteral) throws IOException {};
	public void visit(CharLiteral charLiteral) throws IOException {};
	public void visit(ArrayLiteral arrayLiteral) throws IOException {};
	
	public void visit(Line line) throws IOException {};

	public void visit(Include include) throws IOException {};
	public void visit(Import import1) throws IOException {};
	public void visit(Use use) throws IOException {};

	public void visit(If if1) throws IOException {};
	public void visit(Else else1) throws IOException {};
	public void visit(While while1) throws IOException {};
	public void visit(For for1) throws IOException {};
	public void visit(Foreach foreach) throws IOException {};
	public void visit(FlowControl break1) throws IOException {};

	public void visit(VariableAccess variableAccess) throws IOException {};
	public void visit(MemberAccess memberAccess) throws IOException {};
	public void visit(ArrayAccess arrayAccess) throws IOException {};

	public void visit(VariableDecl variableDecl) throws IOException {
		try {
			for(VariableDeclAtom atom: variableDecl.getAtoms())
				addObject(buildVariableDeclAtom(variableDecl, atom, null));
		} catch (JSONException e) {
			throw new IOException("fail!");
		}
	};
	public void visit(VariableDeclAtom variableDeclAtom) throws IOException {};

	String resolveType(Type type) {
		if(type instanceof FuncType) {
			return "Func";
		} else {
			String tag = type.getName();
			int pointerLevel = type.getPointerLevel();
			while(pointerLevel-- > 0)
				tag = "pointer(" + tag + ")";
			int referenceLevel = type.getReferenceLevel();
			while(referenceLevel-- > 0)
				tag = "reference(" + tag + ")";
			return tag;
		}
	}

	public void visit(FunctionDecl functionDecl) throws IOException {
		try {
			addObject(buildFunctionDecl(functionDecl));
		} catch(JSONException e) {
			throw new IOException("Fail.");
		}
	};

	void addObject(JSONObject obj) throws JSONException {
		JSONArray entry = new JSONArray();
		entry.put(obj.getString("tag"));
		entry.put(obj);
		root.put(entry);
	};

	JSONObject buildFunctionDecl(FunctionDecl node) throws JSONException {
		JSONObject obj = new JSONObject();
		String name = node.getName();
		if(node.getSuffix().length() > 0)
			name = name + "~" + node.getSuffix();
		obj.put("name", name);
		if(node.isMember()) {
			obj.put("tag", "memberFunction(" + node.getTypeDecl().getName() + ", " + name + ")");
			obj.put("type", "memberFunction");
		} else {
			obj.put("tag", name);
			obj.put("type", "function");
		}
		if(node.getComment() != null) {
			obj.put("doc", node.getComment().getContent());
		} else {
			obj.put("doc", JSONObject.NULL);
		}
		if(node.isExtern()) {
			if(!node.isExternWithName()) {
				obj.put("extern", true);
			} else {
				obj.put("extern", node.getExternName());
			}
		} else {
			obj.put("extern", false);
		}
		if(node.isUnmangled()) {
			if(!node.isUnmangledWithName()) {
				obj.put("unmangled", true);
			} else {
				obj.put("unmangled", node.getUnmangledName());
			}
		} else {
			obj.put("unmangled", false);
		}
		obj.put("fullName", node.getFullName());
		JSONArray modifiers = new JSONArray();
		if(node.isStatic())
			modifiers.put("static");
		if(node.isFinal())
			modifiers.put("final");
		if(node.isAbstract())
			modifiers.put("abstract");
		obj.put("modifiers", modifiers);
		/* `genericTypes` */
		JSONArray genericTypes = new JSONArray();
		for(TypeParam typeParam: node.getTypeParams().values()) {
			genericTypes.put(typeParam.getName());
		}
		obj.put("genericTypes", genericTypes);
		/* `arguments` */
		JSONArray arguments = new JSONArray();
		Boolean first = true;
		for(Argument arg: node.getArguments()) {
			if(first && node.hasThis()) {
				/* ignore the `this` argument */
				first = false;
				continue; 
			} else if(first) {
				first = false;
			}
			JSONArray argObj = new JSONArray();
			argObj.put(arg.getName());
			argObj.put(resolveType(arg.getType()));
			if(arg.getType().isConst()) {
				JSONArray mods = new JSONArray();
				mods.put("const");
				argObj.put(mods);
			} else {
				argObj.put(JSONObject.NULL);
			}
			arguments.put(argObj);
		}
		obj.put("arguments", arguments);
		/* `returnType` */
		if(node.getReturnType()	!= Type.getVoid()) {
			obj.put("returnType", resolveType(node.getReturnType()));
		} else {
			obj.put("returnType", JSONObject.NULL);
		}
		return obj;
	}

	JSONObject buildVariableDeclAtom(VariableDecl decl, VariableDeclAtom node, ClassDecl cls) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("name", node.getName());
		if(cls != null) {
			obj.put("tag", "field(" + cls.getName() + ", " + node.getName() + ")");
			obj.put("type", "field");
		} else {
			obj.put("tag", node.getName());
			obj.put("type", "globalVariable");
		}
		JSONArray modifiers = new JSONArray();
		if(decl.isStatic())
			modifiers.put("static");
/*		if(node.isConst())
			modifiers.put("const");*/
		obj.put("modifiers", modifiers);
		if(decl.isExtern()) {
			if(!decl.isExternWithName()) {
				obj.put("extern", true);
			} else {
				obj.put("extern", decl.getExternName());
			}
		} else {
			obj.put("extern", false);
		}
		if(decl.isGlobal()) {
			if(decl.isUnmangled()) {
				if(!decl.isUnmangledWithName()) {
					obj.put("unmangled", true);
				} else {
					obj.put("unmangled", decl.getUnmangledName());
				}
			} else {
				obj.put("unmangled", false);
			}
			obj.put("fullName", decl.getFullName(node));
		}
		obj.put("varType", resolveType(decl.getType()));
		if(node.getExpression() != null) /* TODO: make this work for `:=` */
			obj.put("value", node.getExpression().toString());
		else
			obj.put("value", JSONObject.NULL);
		return obj;
	}
			
	public void visit(ClassDecl node) throws IOException {
		try {
			JSONObject obj = new JSONObject();
			obj.put("name", node.getName());
			obj.put("type", "class");
			obj.put("tag", node.getName());
			obj.put("abstract", node.isAbstract());
			if(node.getSuperRef() != null)
				obj.put("extends", node.getSuperRef().getName());
			else
				obj.put("extends", JSONObject.NULL);
			if(node.getComment() != null) {
				obj.put("doc", node.getComment().getContent());
			} else {
				obj.put("doc", JSONObject.NULL);
			}
			/* `genericTypes` */
			JSONArray genericTypes = new JSONArray();
			for(TypeParam typeParam: node.getTypeParams().values()) {
				genericTypes.put(typeParam.getName());
			}
			obj.put("genericTypes", genericTypes);
			if(node.isUnmangled()) {
				if(!node.isUnmangledWithName()) {
					obj.put("unmangled", true);
				} else {
					obj.put("unmangled", node.getUnmangledName());
				}
			} else {
				obj.put("unmangled", false);
			}
			obj.put("fullName", node.getUnderName());
			/* `members` */
			JSONArray members = new JSONArray();
			for(FunctionDecl function: node.getFunctions()) {
				if(!function.getName().startsWith("__")) {/* exclude "private" functions */
					JSONArray member = new JSONArray();
					member.put(function.getName());
					member.put(buildFunctionDecl(function));
					members.put(member);
				}
			}
			for(VariableDecl decl: node.getVariables()) {
				for(VariableDeclAtom atom: decl.getAtoms()) {
					JSONArray member = new JSONArray();
					member.put(atom.getName());
					member.put(buildVariableDeclAtom(decl, atom, node));
					members.put(member);
				}
			}
			obj.put("members", members);
			addObject(obj);
		} catch(JSONException e) {
			throw new IOException("Fail.");
		}
	};

	public void visit(CoverDecl node) throws IOException {
		try {
			JSONObject obj = new JSONObject();
			obj.put("name", node.getName());
			obj.put("type", "cover");
			obj.put("tag", node.getName());
			if(node.getSuperRef() != null)
				obj.put("extends", node.getSuperRef().getName());
			else
				obj.put("extends", JSONObject.NULL);
			if(node.getFromType() != null)
				obj.put("from", node.getFromType().toString());
			else
				obj.put("from", JSONObject.NULL);
			if(node.getComment() != null) {
				obj.put("doc", node.getComment().getContent());
			} else {
				obj.put("doc", JSONObject.NULL);
			}
			if(node.isUnmangled()) {
				if(!node.isUnmangledWithName()) {
					obj.put("unmangled", true);
				} else {
					obj.put("unmangled", node.getUnmangledName());
				}
			} else {
				obj.put("unmangled", false);
			}
			obj.put("fullName", node.getUnderName());
			/* `members` */
			JSONArray members = new JSONArray();
			for(FunctionDecl function: node.getFunctions())
				if(!function.getName().equals("class")) {
					JSONArray member = new JSONArray();
					member.put(function.getName());
					member.put(buildFunctionDecl(function));
					members.put(member);
				}

			for(CoverDecl addon: node.getAddons()) {
				for(FunctionDecl function: addon.getFunctions())
					if(!function.getName().equals("class")) {
						JSONArray member = new JSONArray();
						member.put(function.getName());
						member.put(buildFunctionDecl(function));
						members.put(member);
					}			
			}
			obj.put("members", members);
			addObject(obj);
		} catch(JSONException e) {
			throw new IOException("Fail.");
		}		
	};

	public void visit(InterfaceDecl interfaceDecl) throws IOException {};

	public void visit(TypeArgument typeArgument) throws IOException {};
	public void visit(RegularArgument regularArgument) throws IOException {};
	public void visit(MemberArgument memberArgument) throws IOException {};
	public void visit(MemberAssignArgument memberArgument) throws IOException {};

	public void visit(Type type) throws IOException {};
	public void visit(BuiltinType builtinType) throws IOException {};

	public void visit(VarArg varArg) throws IOException {};
	
	public void visit(NodeList<? extends Node> list) throws IOException {
		for(Node node: list) {
			node.accept(this);
		}
	};
	public void visit(NodeMap<?, ? extends Node> list) throws IOException {};
	public void visit(MultiMap<?, ?> list) throws IOException {};

	public void visit(Block block) throws IOException {};
	public void visit(VersionBlock versionBlock) throws IOException {};

	public void visit(Cast cast) throws IOException {};

	public void visit(AddressOf addressOf) throws IOException {};
	public void visit(Dereference dereference) throws IOException {};

	public void visit(OpDecl opDecl) throws IOException {};

	public void visit(BinaryCombination binaryCombination) throws IOException {};
	public void visit(Ternary ternary) throws IOException {};

	public void visit(Match match) throws IOException {};
	public void visit(Case case1) throws IOException {};
	
}
