package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.control.For;
import org.ooc.nodes.control.Scope;
import org.ooc.nodes.keywords.ConstKeyword;
import org.ooc.nodes.keywords.Keyword;
import org.ooc.nodes.keywords.StaticKeyword;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.SyntaxNode;
import org.ooc.nodes.others.VariableDecl;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Variable;
import org.ubi.SourceReader;

/**
 * Variable declaration parser 
 * 
 * @author Amos Wenger
 */
public class VariableDeclParser implements Parser {
	
	public boolean parse(final SourceContext context) throws IOException {

		SyntaxNode last = context.getLast();
		if(!(last == null || last instanceof LineSeparator || last instanceof Keyword)) {
			return false;
		}
		
		SourceReader reader = context.reader;

    	if(context.isIn(Scope.class, false) || context.isIn(For.class, false)) {
            return readVarDecl(context, reader);
    	}
        
        return false;
		
	}

	protected boolean readVarDecl(final SourceContext context, SourceReader reader)
			throws EOFException {
		
		boolean isConst = false;
		boolean isStatic = false;
		
		SyntaxNode staticKw = null;
		SyntaxNode constKw = null;
		SyntaxNode last = context.getLast();
		if(last != null) {
			Keyword keyword = last instanceof Keyword ? (Keyword) last
					: last.getNearestPrevTyped(Keyword.class);
			while(keyword != null) {
				if(keyword instanceof ConstKeyword) {
					constKw = keyword;
					isConst = true;
				} else if(keyword instanceof StaticKeyword) {
					staticKw = keyword;
					isStatic = true;
				} else {
					return false; // Unknown keyword. TODO: check if we couldn't throw a CFE in certain cases 
				}
				keyword = keyword.getNearestPrevTyped(Keyword.class);
			}
		}
		
		Type typeUsage = Type.read(context, context.reader);
		reader.skipWhitespace();
	
		String name = reader.readName();
		if(name.isEmpty()) {
			return false;
		}
		VariableDecl decl = new VariableDecl(reader.getLocation(), new Variable(typeUsage, name));
	    decl.variable.isStatic = isStatic;
	    decl.variable.isConst = isConst;
		context.add(decl);
		
		if(staticKw != null) {
			staticKw.drop();
		}
		if(constKw != null) {
			constKw.drop();
		}
		
		return true;
		
	}

}
