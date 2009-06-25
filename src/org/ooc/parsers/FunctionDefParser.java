package org.ooc.parsers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import org.ooc.errors.CompilationFailedError;
import org.ooc.errors.SourceContext;
import org.ooc.nodes.clazz.ClassDef;
import org.ooc.nodes.clazz.Instantiation;
import org.ooc.nodes.clazz.MemberAccess;
import org.ooc.nodes.control.Return;
import org.ooc.nodes.functions.FunctionDef;
import org.ooc.nodes.functions.TypedArgumentList;
import org.ooc.nodes.operators.Assignment;
import org.ooc.nodes.others.Block;
import org.ooc.nodes.others.LineSeparator;
import org.ooc.nodes.others.TransparentBlock;
import org.ooc.nodes.others.VariableAccess;
import org.ooc.nodes.types.Type;
import org.ooc.structures.Clazz;
import org.ooc.structures.Function;
import org.ooc.structures.Variable;
import org.ooc.structures.VariableAlias;
import org.ubi.FileLocation;
import org.ubi.SourceReader;

class FunctionDefParser implements Parser {

	
	public boolean parse(SourceContext context) throws EOFException {

		SourceReader reader = context.reader;
		
        if(context.isIn(FunctionDef.class, true)) {
        	
			int mark = reader.mark();
            if(reader.matches("new", true) && !Character.isLetterOrDigit(reader.peek())) {
            	
            	Instantiation.read(context);
            	return true;
                
            }
            
            reader.reset(mark);
            if(reader.matches("return", true)) {
            	
                context.add(new Return(reader.getLocation()));
                return true;
                
            }
        } else {
        	
    		return parseFunctionDef(context, reader);
        	
        }
        			
        return false;
		
	}
	
	/**
	 * Parse a function definition
	 * @param context
	 * @param reader
	 * @return
	 * @throws EOFException
	 */
	public static boolean parseFunctionDef(SourceContext context, SourceReader reader) throws EOFException {

		ClassDef classDef = context.getNearest(ClassDef.class);
        Type returnType = null;
        String name = "";
        
		reader.skipWhitespace();
		/**
		 * 1st case: constructor definition
		 */
		if(reader.matches("new", true) && !Character.isLetter(reader.peek())) {
        	
            if(classDef == null) {
                throw new CompilationFailedError(reader.getLocation(), 
                		"Error! constructor (new()) outside Class Definition. Sucks to be you :/");
            }
			returnType = classDef.clazz.getType();
            name = "new";
            
        } else {
        
        	reader.skipWhitespace();
        	/**
        	 * 2nd case: normal function definition
        	 */
            returnType = Type.read(context, context.reader);
            if(returnType == null) {
            	return false;
            }
        	reader.skipWhitespace();
        	name = reader.readName();
        	if(name.isEmpty()) {
        		return false;
        	}
        	
        }
        
        if(name.isEmpty()) {
            return false;
        }

        TypedArgumentList args = TypedArgumentList.read(context);
		if(args == null) {
			// No arguments? Ye kidding.
			return false;
		}
		
		throw new CompilationFailedError(reader.getLocation(),
    			"C-style function declaration deprecated. Use 'func methodName(TypeArg arg, TypeArg2 arg2) -> ReturnType' instead, " +
    			"where parenthesis are optional when there are no arguments, and the arrow and return type " +
    			"are optional when the function is Void.");
        
        //return parseFuncArgs(context, reader, classDef, args, returnType, toReturn, name);

    }
	
	/**
	 * Parse the body of a function definition
	 * @param args 
	 */
	public static boolean parseFuncArgs(SourceContext context, SourceReader reader, ClassDef classDef, TypedArgumentList args, Type returnType, Variable toReturn, String name) throws EOFException {
		
		FunctionDef funcDef = new FunctionDef(reader.getLocation(), new Function(name, returnType, classDef == null ? null: classDef.clazz, args));
		
		List<Variable> realArgs = new ArrayList<Variable>();
		
		for(int i = 0; i < funcDef.function.args.list.size(); i++) {
			
			Variable var = funcDef.function.args.list.get(i);
			if(var instanceof VariableAlias) {
				
				if(classDef == null) {
					throw new CompilationFailedError(funcDef.location, "Using the '=field' syntax outside a class definition!");
				}
				addHeritage(realArgs, funcDef, var, args.location, classDef.clazz);
				
			} else {
				realArgs.add(var);
			}
			
		}
		
		args.list.clear();
		args.list.addAll(realArgs);
		
		if(toReturn != null) {
			
			if(classDef == null) {
				throw new CompilationFailedError(funcDef.location, "Using the '=field' syntax outside a class definition!");
			}
			
			funcDef.add(new Return(funcDef.location));
			funcDef.add(new MemberAccess(funcDef.location, classDef.clazz.getThis(), toReturn));
			funcDef.add(new LineSeparator(funcDef.location));
		}

        reader.skipWhitespace();

        if(reader.matches("{", true)) {
        	context.open(funcDef);
        } else {
        	context.add(funcDef);
        }
        
        return true;

    }
	
    private static void addHeritage(List<Variable> list, FunctionDef funcDef, Variable memberVariable, FileLocation location, Clazz clazz) {
    	
        list.add(memberVariable);
        Block superHeritage = new TransparentBlock(location);
        superHeritage.add(new MemberAccess(location, clazz.getThis(), memberVariable));
        superHeritage.add(new Assignment(location));
        superHeritage.add(new VariableAccess(location, memberVariable));
        superHeritage.add(new LineSeparator(location));
        funcDef.add(superHeritage);
        
    }

	
}
