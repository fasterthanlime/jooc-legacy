package org.ubi;

/**
 * A syntax error is at a specific location
 *
 * @author Amos Wenger
 */
public class SyntaxError extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4274111704528892881L;
	
	private FileLocation location;
    private String simpleMessage;

    /**
     * Create a new syntax error at specified location with specified message
     * @param location
     * @param message
     */
    public SyntaxError(FileLocation location, String message) {
        super(message+location);
        this.location = location;
        this.simpleMessage = message;
    }

    /**
     * @return where the error occured in the source
     */
    public FileLocation getLocation() {
        return location;
    }

    /**
     * @return the error message, without the stack trace
     */
    public String getSimpleMessage() {
        return simpleMessage;
    }

}
