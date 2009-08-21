package org.ooc.parsers;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ooc.errors.SourceContext;
import org.ooc.nodes.types.Type;
import org.ooc.nodes.types.TypeReference;
import org.ubi.SourceReader;

/**
 * The type resolver is used to keep track of all types and to return
 * references to them (TypeReference(s) or ClasssReference(s))
 * 
 * @author Amos Wenger
 */
public class TypeParser implements Parser {

	/** A list of common type names, from longest to shortest */
	protected final static List<String> stdTypes;
	/** A map from common type names to an array of their "components" */
	protected final static Map<String, String[]> stdTypesMap;
	
	static {
		
		stdTypes = new ArrayList<String>();
		stdTypesMap = new HashMap<String, String[]>();
		
		addType("unsigned long long int");
		addType("signed long long int");
		addType("unsigned long long");
		addType("signed long long");
		addType("long long");
		
		addType("unsigned long int");
		addType("signed long int");
		addType("unsigned long");
		addType("signed long");
		
		addType("unsigned int");
		addType("signed int");
		addType("int");
		
		addType("unsigned short int");
		addType("signed short int");
		addType("unsigned short");
		addType("signed short");
		addType("short");
		
		addType("unsigned char");
		addType("signed char");
		addType("char");
		
		addType("long double");
		addType("double");
		addType("float");
		addType("unsigned");
		addType("signed");
		
		addType("long");
		addType("bool");
		addType("void");
		addType("size_t");
		addType("wchar_t");
		addType("ptrdiff_t");
		
	}
	
	/**
	 * Add a type to the type parser, so that it recognizes it later
	 * @param typeName
	 */
	public static void addType(String typeName) {

		StringTokenizer st = new StringTokenizer(typeName, " ");
		String[] breakDown = new String[st.countTokens()];
		int count = 0;
		while(st.hasMoreTokens()) {
			breakDown[count++] = st.nextToken();
		}
		stdTypes.add(typeName);
		stdTypesMap.put(typeName, breakDown);
		
	}
	
	/**
	 * Test for a validity of a type
	 * @param typeName
	 * @return
	 */
	public static boolean isValidType(String typeName) {

		return stdTypesMap.containsKey(typeName);
		
	}
	
	
	public boolean parse(SourceContext context) throws IOException {
		
		String name = readTypeName(context.reader);
		if(name.isEmpty()) {
			return false; // Failed to parse
		}
		
		if(Character.isJavaIdentifierPart(context.reader.peek())) {
			// We have a read a type, but it's only the beginning of another
			// word (e.g. a variable name), ignore.
			return false;
		}
		
		Type type = new Type(context.reader.getLocation(), context.source.getRoot(), name);
		context.add(new TypeReference(context.reader.getLocation(), type));
		type.isResolved = true;
		return true;
		
	}

	/**
	 * Read a standard type name, e.g. "unsigned short int" or "long long"
	 * @param context
	 * @return the read type name, or the empty string if nothing corresponds
	 * to a type name
	 * @throws IOException
	 */
	public static String readTypeName(SourceReader reader) throws EOFException {
	
		int mark = reader.mark();
		reference: for(String name: stdTypes) {
			
			String parts[] = stdTypesMap.get(name);
			reader.reset(mark);
			int count = 0;
			for(String part: parts) {
				if(count >= parts.length) {
					continue reference; // too long
				}
				reader.skipWhitespace();
				if(!(reader.matches(part, true) && !Character.isLetterOrDigit(reader.peek()))) {
					continue reference; // must match the part
				}
				count++;
			}
			if(Character.isLetterOrDigit(reader.peek())) {
				return ""; // false alert
			}
			
			return name;
			
		}
		return "";
		
	}

}
