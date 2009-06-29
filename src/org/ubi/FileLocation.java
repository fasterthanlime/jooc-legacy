package org.ubi;


/**
 * Information about a location in a file
 *
 * @author Amos Wenger
 */  
public class FileLocation {
    
	private String fileName;
    private int lineNumber;
    private int linePos;
    private int index;

    /**
     * Default constructor
     * @param fileName
     * @param lineNumber
     * @param linePos
     * @param index
     */
    public FileLocation(String fileName, int lineNumber, int linePos, int index) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.linePos =  linePos;
        this.index = index;
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

    
    @Override
	public String toString() {
        return " "+fileName+":"+getLineNumber()+":"+getLinePos();
    }
    
}