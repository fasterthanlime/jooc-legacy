package org.ooc.nodes.doc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.ooc.nodes.others.SyntaxNode;
import org.ubi.FileLocation;

/**
 *
 * @author Amos Wenger
 */
public class MultiLineComment extends SyntaxNode implements Comment {

	private final String before;
	private final String after;
	private final ArrayList<String> lines;
	
	/**
	 * Default constructor
	 * @param location
	 * @param before
	 * @param content
	 * @param after
	 */
    public MultiLineComment(FileLocation location, String before, String content, String after) {
    	
        super(location);
        this.before = before;
        
        StringTokenizer st = new StringTokenizer(content, "\n");
        lines = new ArrayList<String>();
        while(st.hasMoreTokens()) {
        	String trimmed = st.nextToken().trim();
        	if(trimmed.startsWith("*")) {
        		trimmed = trimmed.substring(1);
        	}
        	trimmed = trimmed.trim();
        	if(!trimmed.isEmpty()) {
        		lines.add(trimmed);
        	}
        }
        this.after = after;
        
    }
    
    @Override
	public void writeToCSource(Appendable a) throws IOException {
    	
    	a.append(before);
    	
		writeIndent(a);
    	a.append(getPrelude());
    	for(String line: lines) {
    		a.append('\n');
    		writeIndent(a);
    		a.append(" * ");
    		a.append(line);
    	}
    	a.append('\n');
		writeIndent(a);
		a.append(" */");
		
		a.append(after);
		
	}

	protected String getPrelude() {

		return "/*";
		
	}

	@Override
	public String getDescription() {
    	return toString()+location;
    }

	@Override
	public String getContent() {
		
		StringBuilder builder = new StringBuilder();
		int counter = 0;
		for(String line: lines) {
			builder.append(line);
			if(++counter < lines.size()) {
				builder.append('\n');
			}
		}
		return builder.toString();
		
	}

}
