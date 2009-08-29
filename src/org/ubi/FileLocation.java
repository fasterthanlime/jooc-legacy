package org.ubi;


/**
 * Information about a location in a file
 *
 * @author Amos Wenger
 */  
public class FileLocation {
    
	protected String fileName;
    protected int lineNumber;
    protected int linePos;
    protected int index;
    protected int length;

    public FileLocation(String fileName, int lineNumber, int linePos, int index) {
    	this(fileName, lineNumber, linePos, index, 1);
    }
    
    public FileLocation(String fileName, int lineNumber, int linePos, int index, int length) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.linePos =  linePos;
        this.index = index;
        this.length = length;
    }

    /**
     * @return the name of the file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @return the position in line (e.g. number of characters after the last newline)
     */
    public int getLinePos() {
        return linePos;
    }

    /**
     * @return the number of characters (including whitespace) since the beginning of the file
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * @return the length of the interesting location, in characters
     */
    public int getLength() {
		return length;
	}

    
    @Override
	public String toString() {
        return " "+fileName+":"+getLineNumber()+":"+getLinePos();
    }
    
}