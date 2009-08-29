package org.ubi;


import java.io.IOException;


/**
 * Error issued when the compilation process has failed in an unrecoverable way.
 * 
 * @author Amos Wenger
 */
public class CompilationFailedError extends Error {

	/**
	 * 
	 */
	protected static final long serialVersionUID = 2065923195896492204L;
	
	protected boolean printed;

	protected final FileLocation location;
	
	protected String line;
	protected String cursor;

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
		fillLine(location);
		
	}
	
	protected void fillLine(FileLocation location) {
		if(location != null) {
			try {
				
				SourceReader reader = SourceReader.getReaderFromPath(location.getFileName());
				this.line = reader.getLine(location.getLineNumber());
				
				StringBuffer sb = new StringBuffer(line.length());
				for(int i = 0; i < location.linePos - 1; i++) {
					char c = line.charAt(i);
					if(c == '\t') {
						sb.append('\t');
					} else {
						sb.append(' ');
					}
				}
				for(int i = 0; i < location.length; i++) sb.append("^");
				cursor = sb.toString();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	
	@Override
	public String toString() {

		return getMessage();
		
	}
	
	@Override
	public String getMessage() {

		if(line == null) {
			return super.getMessage();
		}
		
		return "\n" + super.getMessage().trim() + "\n" + line + cursor;
		
	}
	
}
