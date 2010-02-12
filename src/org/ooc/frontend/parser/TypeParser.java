package org.ooc.frontend.parser;

import org.ooc.frontend.model.Access;
import org.ooc.frontend.model.Declaration;
import org.ooc.frontend.model.Expression;
import org.ooc.frontend.model.FuncType;
import org.ooc.frontend.model.Module;
import org.ooc.frontend.model.NodeList;
import org.ooc.frontend.model.Type;
import org.ooc.frontend.model.TypeAccess;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.frontend.model.tokens.TokenReader;
import org.ooc.frontend.model.tokens.Token.TokenType;
import org.ubi.CompilationFailedError;
import org.ubi.SourceReader;

public class TypeParser {
	public static Type parse(Module module, SourceReader sReader, TokenReader reader) {
		return parse(module, sReader, reader, true);
	}

	public static Type parse(Module module, SourceReader sReader, TokenReader reader, boolean namespaceNeedsParens) {	
		
		boolean hasParens = false;
		String name = "";
		String namespace = null;
		int pointerLevel = 0;
		int referenceLevel = 0;
		boolean isArray = false;
		NodeList<Access> typeParams = null;
		Expression arraySize = null;
		
		Token startToken = reader.peek();
		boolean isConst = false;
		
		//TODO add more checks
		while(reader.hasNext()) {
			Token t = reader.peek();
			if(t.type == TokenType.OPEN_PAREN) {
				if(hasParens) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Only one pair of parens, please.");
				}
				reader.skip();
				hasParens = true;
			} else if(t.type == TokenType.UNSIGNED) {
				reader.skip();
				name += "unsigned ";
			} else if(t.type == TokenType.SIGNED) {
				reader.skip();
				name += "signed ";
			} else if(t.type == TokenType.LONG) {
				reader.skip();
				name += "long ";
			} else if(t.type == TokenType.STRUCT) {
				reader.skip();
				name += "struct ";
			} else if(t.type == TokenType.UNION) {
				reader.skip();
				name += "union ";
			} else if(t.type == TokenType.CONST_KW) {
				reader.skip();
				isConst = true;
			} else break;
		}
		
		if(reader.peek().type == TokenType.NAME) {
			name += reader.read().get(sReader);
		}

		if(reader.peek().type == TokenType.NAME && (hasParens || !namespaceNeedsParens)) {
			// namespacey! what we read before is the namespace actually.
			namespace = name;
			name = reader.read().get(sReader);
		}

		if(reader.peek().type == TokenType.LESSTHAN) {
			typeParams = readTypeParams(module, sReader, reader, typeParams);
		}
		
		if(name.equals("Func")) {
			FuncType funcType = new FuncType(startToken);
			ArgumentParser.fill(module, sReader, reader, true, funcType.getDecl().getArguments());
			if(reader.peek().type == TokenType.ARROW) {
				reader.read();
				funcType.getDecl().setReturnType(TypeParser.parse(module, sReader, reader));
				if(funcType.getDecl().getReturnType() == null) {
					throw new CompilationFailedError(sReader.getLocation(reader.peek()),
							"Expected function pointer return type after the arrow '->'");
				}
			}
			return funcType;
		}

		while(reader.peek().type == TokenType.OPEN_SQUAR) {
			reader.skip();
			Expression expr = ExpressionParser.parse(module, sReader, reader);
			if(reader.read().type != TokenType.CLOS_SQUAR) {
				return null;
			}
			pointerLevel++;
			isArray = true;
			if(expr != null) {
				arraySize = expr;
			}
		}
		
		while(reader.peek().type == TokenType.STAR) {
			pointerLevel++;
			reader.skip();
		}
		
		while(reader.peek().type == TokenType.AT) {
			referenceLevel++;
			reader.skip();
		}

		if(hasParens) {
			if(reader.peek().type == TokenType.CLOS_PAREN) {
				reader.skip();
			} else {
				throw new CompilationFailedError(sReader.getLocation(reader.peek()),
						"Close your parens please.");
			}
		}
		
		if(name.length() > 0) {
			Type type = new Type(name.trim(), pointerLevel, referenceLevel, startToken);
			if(name.equals("This")) {
				if(!module.parseStack.isEmpty()) {
					Declaration decl = (Declaration) module.parseStack.peek();
					type.setRef(decl);
					type.setName(decl.getName());
				}
			}
			type.setNamespace(namespace);
			type.setArray(isArray);
			type.setConst(isConst);
			if(typeParams != null) {
				type.getTypeParams().addAll(typeParams);
			}
			if(arraySize != null) {
				type.setArraySize(arraySize);
			}
			return type;
		}
		return null;
		
	}

	private static NodeList<Access> readTypeParams(Module module,
			SourceReader sReader, TokenReader reader,
			NodeList<Access> _typeParams) {
		
		NodeList<Access> typeParams = _typeParams;
		
		reader.skip();
		while(reader.peek().type != TokenType.GREATERTHAN) {
			Access innerType = null;
			
			Type type = TypeParser.parse(module, sReader, reader);
			if(type != null) innerType = new TypeAccess(type, type.startToken);
			
			if(innerType == null) {
				innerType = AccessParser.parse(module, sReader, reader);
			}
			if(innerType == null) {
				typeParams = null;
				break;
			}
			if(typeParams == null) typeParams = new NodeList<Access>(); 
			typeParams.add(innerType);
			if(reader.peek().type != TokenType.COMMA) break;
		}
		if(reader.read().type != TokenType.GREATERTHAN) {
			typeParams = null;
		}
		
		return typeParams;
		
	}
	
}
