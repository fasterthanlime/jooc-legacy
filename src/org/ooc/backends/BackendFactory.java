package org.ooc.backends;


/**
 * The backend factory knows every backend by their sweet name, and
 * is able to summon them on a sign of the hand from the user. 
 * 
 * @author Amos Wenger
 */
public class BackendFactory {

	/**
	 * Return the backend specified by this option.
	 * @param input Something in the form of for example "gcc,-v,-clean=yes"
	 * or "make,-link=mylib.a"
	 * @return
	 */
	public static Backend getBackend(String input) {
		
		String backendString = input.trim();
		int index = backendString.indexOf(",");
		String params;
		if(index == -1) {
			params = "";
		} else {
			params = backendString.substring(index + 1);
		}
		
		if(backendString.startsWith("gcc")) {
			return new GccBackend(params);
		} else if(backendString.startsWith("make")) {
			return new MakeBackend(params);
		} else if(backendString.startsWith("none")) {
			return null;
		}
		
		System.err.println("Unknown backend: "+backendString);
		return null;
		
	}
	
}
