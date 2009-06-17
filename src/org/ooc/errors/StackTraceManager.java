package org.ooc.errors;

/**
 * Utility functions to format stack trace messages 
 * 
 * @author Amos Wenger
 */
public class StackTraceManager {

	/**
	 * 
	 * @param message by what the stack trace message will begin
	 * @param offset how many stack elements you don't care about (not counting getStackTrace itself,
	 * which is excluded automatically).
	 * @param bound if bound isn't null and a stack trace element contains bound, it will stop 
	 * the StackTrace there and append "..."
	 * @return
	 */
	public static String getStackTrace(String message, int offset, String bound) {
		
		StringBuilder builder = new StringBuilder(message);
		builder.append("\n");
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for(int i = offset + 1; i < elements.length; i++) {
    		String string = elements[i].toString();
    		if(bound != null && string.contains(bound)) {
    			//builder.append("\t...\n");
    			break;
    		}
    		builder.append("\tat ");
			builder.append(string);
			builder.append('\n');
    	}
		return builder.toString();

	}

	
	
}
