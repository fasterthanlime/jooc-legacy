package org.ooc.errors;

import org.ubi.FileLocation;

/**
 * Error issued when the compilation process has failed in an unrecoverable way.
 * 
 * @author Amos Wenger
 */
public class CompilationFailedError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2065923195896492204L;
	
	private boolean printed;

	private final FileLocation location;

	/**
	 * Default constructor from another throwable
	 * @param throwable
	 */
	public CompilationFailedError(Throwable throwable) {
		super(throwable);
		this.location = null;
	}
	
	/**
	 * Default constructor with message
	 * @param message
	 */
	public CompilationFailedError(FileLocation location, String message) {
		super(location == null ? message : location + ": " + message);
		this.location = location;
	}
	
	@Override
	public void printStackTrace() {
	
		System.err.println(getMessage());
		if(!printed) {
			printed = true;
			super.printStackTrace();
		}
		
	}
	
	/**
	 * @return the location
	 */
	public FileLocation getLocation() {
		return location;
	}
	
}
