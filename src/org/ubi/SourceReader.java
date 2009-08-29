package org.ubi;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to read blocks of text.
 * Mostly useful for keeping track of line number/positions (for accurate
 * error messages, @see SyntaxError).
 * Has builtin methods for reading C-like elements/tokens, like
 * string literals/char literals, blocks, etc.
 * @author Amos Wenger
 */
public class SourceReader {
	
	/**
	 * The case sensibility setting, e.g. whether 'A' == 'a' or 'A' != 'a'
	 * @author Amos Wenger
	 */
    public enum CaseSensibility {
    	/** Don't make a difference between capitalized characters and others, e.g. 'A' != 'a' */
        SENSITIVE,
        /** Distinguish between capitalized characters and others, e.g. 'A' == 'a' */
        INSENSITIVE
    }

    protected ArrayList<Integer> newlineIndices;
    protected String fileName;
    protected char[] content;
    protected int index;
    protected int mark;

    /**
     * Read the content of a the file at place "path"
     * @param path The path of the file to be read
     * @return a SourceReader reading from the file content
     * @throws java.io.IOException if file can't be found or opened for reading
     * (or any other I/O exception, for that matter).
     */
    public static SourceReader getReaderFromPath(String path) throws IOException {
        return getReaderFromFile(new File(path));
    }

    /**
     * Read the content of a the file pointed by "file"
     * @param file The file object from which to read
     * @return a SourceReader reading from the file content
     * @throws java.io.IOException if file can't be found or opened for reading
     * (or any other I/O exception, for that matter).
     */
    public static SourceReader getReaderFromFile(File file) throws IOException {
        return new SourceReader(file.getPath(), readToString(file));
    }
    
    /**
     * Read the content of a string
     * @param path The path this string came from. Can be an URL, a file path, etc.
     * anything descriptive, really, even "<system>" or "<copy-protected>" ^^
     * @param content
     * @return
     */
    public static SourceReader getReaderFromText(String path, String content) {
		return new SourceReader(path, content);
	}
    
    /**
     * Read the content of a the file pointed by "file"
     * @param file The file object from which to read
     * @return a SourceReader reading from the file content
     * @throws java.io.IOException if file can't be found or opened for reading
     * (or any other I/O exception, for that matter).
     */
    public static String readToString(File file) throws IOException {
        char[] buffer = new char[8192];
        FileReader fR = new FileReader(file);
        StringBuilder content = new StringBuilder((int) file.length());
        int length;
        while((length = fR.read(buffer)) != -1) {
            content.append(buffer, 0, length);
        }
        return content.toString();
    }


    /**
     * Create a new SourceReader
     * @param filePath The filepath is used in locations, for accurate
     * error messages @see SyntaxError
     * @param content The content to read from.
     */
    protected SourceReader(String filePath, String content) {
        this.fileName = filePath;
        this.content = content.toCharArray();
        this.index = 0;
        this.mark = 0;
        this.newlineIndices = new ArrayList<Integer> ();
    }

    /**
     * Read one character from the source at current position.
     * @return The character read
     * @throws EOFException When the end of the file is reached.
     */
    public char read() throws EOFException {
        if(index + 1 > content.length) {
            throw new EOFException("Parsing ended. Parsed"+index
            		+" chars, "+getLineNumber()+" lines total.");
        }
        char character = content[index++];
        if(character == '\n') {
            if(newlineIndices.isEmpty() || newlineIndices.get(newlineIndices.size() - 1).intValue() < index) {
                newlineIndices.add(new Integer(index));
            }
        }
        return character;
    }
    
    /**
     * Read one character from the source at current position, without advancing
     * the pointer
     * @return The character read
     * @throws EOFException When the end of the file is reached.
     */
    public char peek() {

    	return content[index];
    	
	}


    /**
     * Save the current position, allowing it to be restored later with the reset()
     *
     * <i>Note : functions from SourceReader may call mark(), thus overwriting the saved
     * position. If you want to be safe, assign the return value from mark() to an int,
     * which you can later pass to reset(int).</i>
     *
     * Example :
     * <code>
     * int mark = sourceReader.mark();
     * sourceReader.readUntil(...);
     * sourceReader.reset(mark);
     * </code>
     *
     * @return The current position
     */
    public int mark() {
        this.mark = index;
        return mark;
    }

    /**
     * Restore position to the last saved with mark()
     */
    public void reset() {
        this.index = this.mark;
    }

    /**
     * Restore position to the given one
     * @param index The position to jump to
     */
    public void reset(int index) {
        this.index = index;
    }

    /**
     * Rewind position from given offset.
     * (Subtracts offset from index)
     * @param index The position to jump to
     */
    public void rewind(int offset) {
        this.index -= offset;
    }
    
    /**
     * Advance position from given offset.
     * @param offset
     * @throws EOFException 
     */
    public void skip(int offset) throws EOFException {
    	if(offset < 0) {
    		rewind(-offset);
    	} else {
    		for(int i = 0; i < offset; i++) {
    			read();
    		}
    	}
	}

    /**
     * @return the current line number
     */
    public int getLineNumber() {
        int lineNumber = 0;
        while(lineNumber < newlineIndices.size() && newlineIndices.get(lineNumber).intValue() <= index) {
            lineNumber++;
        }
        return lineNumber + 1;
    }

    /**
     * @return the current position in line (e.g. number of characters since the last newline
     */
    public int getLinePos() {
        int lineNumber = getLineNumber();
        if(lineNumber == 1) {
            return index + 1;
        }
		return index - newlineIndices.get(getLineNumber() - 2).intValue() + 1;
    }

    /**
     * @return false if positioned at the end of the content.
     */
    public boolean hasNext() {
        return (index + 1 < content.length);
    }

    /**
     * @return the current file location, containing the file number, line position, and index
     */
    public FileLocation getLocation() {
        return new FileLocation(fileName, getLineNumber(), getLinePos(), index);
    }
    
    public FileLocation getLocation(Locatable loc) throws EOFException {
    	return getLocation(loc.getStart(), loc.getLength());
    }
    
    public FileLocation getLocation(int start, int length) throws EOFException {
    	int mark = mark();
    	reset(0);
    	skip(start);
    	FileLocation loc = getLocation();
    	loc.length = length;
    	reset(mark);
		return loc;
	}

    /**
     * @param character
     * @return true if the last-but-one char equals 'character'.
     */
    public boolean backMatches(char character, boolean trueIfStartpos) {
        if(index <= 0) {
            return trueIfStartpos;
        }
		return content[index - 1] == character;
    }

    /**
     * Test if each candidate in "candidates" matches the next characters in the content.
     * @param candidates
     * @param keepEnd If false, will reset to the initial position before returning.
     * If true, will stay after the matched candidate.
     * @return -1 if no candidate matched. Otherwise, the index of the first matching candidate.
     * @throws java.io.EOFException
     * @throws java.io.IOException
     */
    public int matches(List<String> candidates, boolean keepEnd) throws EOFException {
        int match = -1;
        int count = 0;
        search: for(String candidate: candidates) {
            if(matches(candidate, keepEnd, CaseSensibility.SENSITIVE)) {
                match = count;
                break search;
            }
            ++count;
        }
        return match;
    }
    
    /**
     * Test if a "candidate" matches the next character in the content, and if there's
     * whitespace after it.
     * @param candidate
     * @param keepEnd
     * @return
     * @throws EOFException
     */
    public boolean matchesSpaced(String candidate, boolean keepEnd) throws EOFException {
    	int mark = mark();
    	boolean result = matches(candidate, true) && hasWhitespace(false);
    	if(!keepEnd) {
    		reset(mark);
    	}
    	return result;
    }
    
    /**
     * Test if a "candidate" matches the next character in the content, and if there's
     * characters other than "A-Za-z0-9_" after iti
     * @param candidate
     * @param keepEnd
     * @return
     * @throws EOFException
     */
    public boolean matchesNonident(String candidate, boolean keepEnd) throws EOFException {
    	int mark = mark();
    	boolean result = matches(candidate, true);
    	char c = peek();
    	result &= !((c == '_') || Character.isLetterOrDigit(c));
    	if(!keepEnd) {
    		reset(mark);
    	}
    	return result;
    }

    /**
     * Test if a "candidate" matches the next characters in the content.
     * It is case-sensitive by default
     * @param candidate
     * @param keepEnd If false, will reset to the initial position before returning.
     * If true, will stay after the matched candidate.
     * @return true if the candidate matches, false otherwise.
     * 
     */
    public boolean matches(String candidate, boolean keepEnd) throws EOFException {
        return matches(candidate, keepEnd, CaseSensibility.SENSITIVE);
    }

    /**
     * Test if a "candidate" matches the next characters in the content.
     * @param candidate
     * @param keepEnd If false, will reset to the initial position before returning.
     * If true, will stay after the matched candidate.
     * @param caseMode either Case.SENSITIVE or Case.INSENSITIVE
     * @return true if the candidate matches, false otherwise.
     */
    public boolean matches(String candidate, boolean keepEnd, CaseSensibility caseMode) throws EOFException {

        mark();
        int i = 0;
        char c, c2;
        boolean result = true;
        while(i < candidate.length()) {
            c = read();
            c2 = candidate.charAt(i);
            if(c2 != c) {
                if((caseMode == CaseSensibility.SENSITIVE) || (Character.toLowerCase(c2) != Character.toLowerCase(c))) {
                    result = false;
                    break;
                }
            }
            i++;
        }
        if(!result || !keepEnd) {
            reset();
        }

        return result;

    }

    /**
     * Read a C-style name (a string containing [A-Za-z0-9_] characters) and return it.
     * @return the read name
     */
    public boolean skipName() throws EOFException {

        if(hasNext()) {
            char chr = read();
            if(!Character.isLetter(chr) && chr != '_') { 
                rewind(1);
                return false;
            }
        }

        read : while(hasNext()) {
            char chr = read();
            if(!Character.isLetterOrDigit(chr) && chr != '_' && chr != '!') {
            	rewind(1);
                break read;
            }
        }

        return true;

    }
    
    /**
     * Read a C-style name (a string containing [A-Za-z0-9_] characters) and return it.
     * @return the read name
     */
    public String readName() throws EOFException {

        StringBuilder sB = new StringBuilder();

        mark();
        if(hasNext()) {
            char chr = read();
            if(Character.isLetter(chr) || chr == '_') {
                sB.append(chr);
            } else {
            	rewind(1);
                return "";
            }
        }

        read : while(hasNext()) {
            mark();
            char chr = read();
            if(Character.isLetterOrDigit(chr) || chr == '_' || chr == '!') {
                sB.append(chr);
            } else {
            	rewind(1);
                break read;
            }
        }

        return sB.toString();

    }

    /**
     * Read until a newline character and return the read input
     * @return the read input
     */
    public String readLine() throws EOFException {

        return readUntil('\n', true);

    }

    /**
     * Read a C-style single-line comment (ignore a line).
     * C-style single-line comments are prefixed by "//"
     */
    public void readSingleComment() throws EOFException {
        readLine();
    }

    /**
     * Read a C-style multi-line comment (ignore until "*\/").
     * C-style multi-line comments are prefixed by "/*" and "*\/"
     */
    public void readMultiComment() throws EOFException {
        while(!matches("*/", true, CaseSensibility.SENSITIVE)) { read(); }
    }
    
    /**
     * Read as many times candidates as we can ! Ignoring any char
     * in 'ignored'.
     * @param candidates
     * @param ignored
     * @param keepEnd
     * @return
     */
    public String readMany(String candidates, String ignored, boolean keepEnd) throws EOFException {

        StringBuilder sB = new StringBuilder();

        int myMark = mark();
        while(hasNext()) {
            char c = read();
            if(candidates.indexOf(c) != -1) {
                sB.append(c);
            } else if(ignored.indexOf(c) != -1) {
                // look up in the sky, and think of how lucky you are and others aren't.
            } else {
            	if(keepEnd) {
            		rewind(1); // We went one too far.
            	}
                break;
            }
        }

        if(!keepEnd) {
            reset(myMark);
        }

        return sB.toString();

    }

    /**
     * Read a C-style character literal, e.g. any character or an escape sequence,
     * and return it as a char.
     */
    @SuppressWarnings("fallthrough")
	public char readCharLiteral() throws EOFException, SyntaxError {

        mark();
        char c = read();
        switch(c) {
            case '\'':
                throw new SyntaxError(getLocation(), "Empty char literal !");
            case '\\':
                char c2 = read();
                switch(c2) {
                    case '\\': // backslash
                        c = '\\'; break;
                    case '0': // null char
                        c = '\0'; break;
                    case 'n': // newline
                        c = '\n'; break;
                    case 't': // tab
                        c = '\t'; break;
                    case 'b': // backspace
                        c = '\b'; break;
                    case 'f': // form feed
                        c = '\f'; break;
                    case 'r': // carriage return
                        c = '\r'; break;
                    case '\'': // simple quote
                        c = '\''; break;
                    default:
                    	throw new SyntaxError(getLocation(), "Invalid escape sequence : \\"+spelled(c));
                }
            // intentional fallthrough
            default:
                c2 = read();
                if(c2 != '\'') {
                	throw new SyntaxError(getLocation(), "Char literal too long. Expected ', found "+spelled(c2));
                }
                return c;
        }
        
    }
    
    /**
     * Parse a C-style character literal from an input string, e.g. any character
     * or an escape sequence, and return it as a char.
     */
    @SuppressWarnings("fallthrough")
	public static char parseCharLiteral(String input) throws SyntaxError {
    	
        char c = input.charAt(0);
        int supposedLength = 1;
        switch(c) {
            case '\'':
                throw new SyntaxError(null, "Empty char literal !");
            case '\\':
            	supposedLength++;
                char c2 = input.charAt(1);
                switch(c2) {
                    case '\\': // backslash
                        c = '\\'; break;
                    case '0': // null char
                        c = '\0'; break;
                    case 'n': // newline
                        c = '\n'; break;
                    case 't': // tab
                        c = '\t'; break;
                    case 'b': // backspace
                        c = '\b'; break;
                    case 'f': // form feed
                        c = '\f'; break;
                    case 'r': // carriage return
                        c = '\r'; break;
                    case '\'': // simple quote
                        c = '\''; break;
                    default:
                    	throw new SyntaxError(null, "Invalid escape sequence : \\"+spelled(c));
                }
            // intentional fallthrough
            default:
                if(input.length() > supposedLength) {
                	throw new SyntaxError(null, "Char literal too long.");
                }
                return c;
        }
    	
	}
    
    public static String parseStringLiteral(String string) {

    	int index = 0;
    	StringBuilder buffer = new StringBuilder();
        char c;
        while(index < string.length()) {
            c = string.charAt(index++);
            switch(c) {
                case '\\':
                    char c2 = string.charAt(index++);
                    switch(c2) {
                        case '\\': // backslash
                            buffer.append('\\'); break;
                        case '0': // null char
                            buffer.append('\0'); break;
                        case 'n': // newline
                            buffer.append('\n'); break;
                        case 't': // tab
                            buffer.append('\t'); break;
                        case 'b': // backspace
                            buffer.append('\b'); break;
                        case 'f': // form feed
                            buffer.append('\f'); break;
                        case 'r': // return
                            buffer.append('\r'); break;
                        default: // delimiter
                            buffer.append(c2); break;
                    }
                    break;
                default:
                	buffer.append(c);
            }
        }

        return buffer.toString();
    	
	}

    /**
     * Read a C-like string literal, e.g. enclosed by '"', and with C-like escape sequences,
     * and return it.
     * Note: eats the final '"', no need to skip it.
     */
    public String readStringLiteral() throws EOFException {
        return readStringLiteral('"');
    }

    /**
     * Read a string literal, e.g. enclosed by "delimiter", and with C-like escape sequences,
     * and return it.
     * Note: eats the final '"', no need to skip it.
     * @param delimiter The delimitr, e.g. " (C-like), or ' (e.g. Python-like)
     */
    public String readStringLiteral(char delimiter) throws EOFException {

        StringBuilder buffer = new StringBuilder();
        char c;
        reading : while(true) {
            mark();
            c = read();
            switch(c) {
                case '\\':
                    char c2 = read();
                    switch(c2) {
                        case '\\': // backslash
                            buffer.append('\\'); break;
                        case '0': // null char
                            buffer.append('\0'); break;
                        case 'n': // newline
                            buffer.append('\n'); break;
                        case 't': // tab
                            buffer.append('\t'); break;
                        case 'b': // backspace
                            buffer.append('\b'); break;
                        case 'f': // form feed
                            buffer.append('\f'); break;
                        case 'r': // return
                            buffer.append('\r'); break;
                        default: // delimiter
                            if(c2 == delimiter) { // freakin' java switches. *growl*
                                buffer.append(delimiter);
                            } break;
                    }
                    break;
                default: // TODO : wonder if newline is a syntax error in a string literal
                	if(c == delimiter) {
                		break reading;
                	}
                    buffer.append(c);
            }
        }

        return buffer.toString();

    }

    /**
     * Return true if there's any whitespace after the current position.
     * @param keep If true, will have the same effect as skipWhitespace
     * If false, the position will be left unchanged.
     * @return true if there was any whitespace.
     * @throws java.io.IOException Go look in the closet, 3rd door left.
     */
    public boolean hasWhitespace(boolean skip) throws EOFException {

        boolean has = false;
        int myMark = mark();
        while(hasNext()) {
            int c = read();
            if(Character.isWhitespace(c)) {
            	has = true;
            } else {
            	rewind(1);
            	break;
            }
        }

        if(!skip) {
            reset(myMark);
        }

        return has;

    }

    /**
     * Ignore the next characters which are whitespace (e.g. spaces, tabulations,
     * newlines, linefeeds, ie. anything for which Character.isWhitespace(int) is true.
     * @throws java.io.IOException
     */
    public boolean skipWhitespace() throws EOFException {

        while(hasNext()) {
            int myMark = mark();
            int c = read();
            if(!Character.isWhitespace(c)) {
                reset(myMark);
                break;
            }
        }
        return true;

    }
    
    /**
     * Ignore the next characters which are contained in the string 'chars'
     * @throws java.io.IOException
     */
    public boolean skipChars(String chars) throws EOFException {

        while(hasNext()) {
            int myMark = mark();
            int c = read();
            if(chars.indexOf(c) == -1) {
                reset(myMark);
                break;
            }
        }
        return true;

    }

    /**
     * Skip the next characters until a newline.
     * @throws java.io.EOFException
     */
    public void skipLine() throws EOFException {
        while(read() != '\n') {
        	// Go on with the loop, don't look back.
        }
    }

    /**
     * Read until the character "chr", and return the characters read.
     * Example:
     * <code>
     * String myLine = sourceReader.readUntil(';');
     * </code>
     * @param chr The end delimiter.
     * @throws java.io.EOFException
     */
    public String readUntil(char chr) throws EOFException {
        return readUntil(chr, false);
    }

    /**
     * Read until the character "chr", and return the characters read.
     * @param chr The end delimiter.
     * @param keepEnd If false, leave the position before the end delimiter.
     * If true, include the delimiter in the returned String, and leave the
     * position after.
     * @throws java.io.EOFException
     */
    public String readUntil(char chr, boolean keepEnd) throws EOFException {

        StringBuilder sB = new StringBuilder();

        char chrRead = 0;
        while(hasNext() && (chrRead = read()) != chr) {
            sB.append(chrRead);
        }
        if(!keepEnd) {
            reset(index - 1); // chop off the last character
        } else if(chrRead != 0) {
            sB.append(chr);
        }
        
        return sB.toString();

    }

    /**
     * Read until one of the Strings in "matches" matches, and return the characters read.
     * By default, do not include the matching end delimiter in the resulting String, and leave
     * the position before the matching end delimiter.
     * @param readUntil The potential end delimiters
     * @throws java.io.EOFException
     */
    public String readUntil(String[] matches) throws EOFException {
        return readUntil(matches, false);
    }

    /**
     * Read until one of the Strings in "matches" matches, and return the characters read.
     * @param readUntil The potential end delimiters
     * @param keepEnd If false, leave the position before the matching end delimiter.
     * If true, include the matching delimiter in the returned String, and leave the
     * position after.
     * @throws java.io.EOFException
     */
    public String readUntil(String[] matches, boolean keepEnd) throws EOFException {

        StringBuilder sB = new StringBuilder();
        
        try { while(hasNext()) {
            for(String match: matches) {
                if(matches(match, keepEnd, CaseSensibility.SENSITIVE)) {
                    if(keepEnd) {
                        sB.append(match);
                    }
                    return sB.toString();
                }
            }
            sB.append(read());
        } } catch(EOFException e) {
        	// Normal operation.
        }

        return sB.toString();

    }

    /**
     * Read until the end of file, and return the result.
     */
    public String readUntilEOF() {

        StringBuilder output = new StringBuilder();
        
        try { while(hasNext()) {
           output.append(read());
        } } catch(EOFException e) {
        	// Well, that's the point
        }

        return output.toString();

    }

    /**
     * Read a block delimited by "start" and "end". It deals with nested blocks, e.g.
     * with '{' and '}', it will match '{{}}' in one piece.
     * Note : the final end delimiter is eaten, No need to skip it.
     * @param startChr the start delimiter
     * @param endChr the end delimiter
     * @return the content of the block
     * @throws org.ubi.SyntaxError
     * @throws java.io.IOException
     */
    public String readBlock(char startChr, char endChr) throws SyntaxError, EOFException {
        return readBlock(startChr, endChr, '\0');
    }

    /**
     * Read a block delimited by "start" and "end" delimiters. It deals with nested blocks, e.g.
     * with '{' and '}', it will match '{{}}' in one piece.
     * The escape character (escapeChar) allows to include the endDelimiter in the block,
     * e.g. with '"' and '"' delimiters, and '\\' escapeChar, there can be escape sequence in
     * what looks obviously like a String literal.
     * Note : the final end delimiter is eaten, No need to skip it.
     * @param startChr the start delimiter
     * @param endChr the end delimiter
     * @return the content of the block
     * @throws org.ubi.SyntaxError
     * @throws java.io.IOException
     */
    public String readBlock(char startChr, char endChr, char escapeChar) throws SyntaxError, EOFException {

        skipWhitespace();
        mark();
        char c;
        if((c = read()) != startChr) {
            reset();
            throw new SyntaxError(getLocation(), "Trying to read block delimited by "
            		+spelled(startChr)+spelled(endChr)
            		+", but "+spelled(c)+" found instead.");
        }

        StringBuilder output = new StringBuilder();

        int count = 1;
        char chr;

        try { reading: while(true) {
            chr = read();
            if(chr == escapeChar) {
                output.append(chr);
                chr = read();
            }

            if(chr == endChr) {
                if(--count <= 0) {
                    break reading;
                }
            } else if(chr == startChr) {
                ++count;
            }
            output.append(chr);
        } } catch(EOFException e) {
        	// Normal operation
        }

        return output.toString();

    }

    /**
     * Read a block delimited by "start" and "end" delimiters. It deals with nested blocks, e.g.
     * with '{' and '}', it will match '{{}}' in one piece.
     * The escape character (escapeChar) allows to include the endDelimiter in the block,
     * e.g. with '"' and '"' delimiters, and '\\' escapeChar, there can be escape sequence in
     * what looks obviously like a String literal.
     * Note : the final end delimiter is eaten, No need to skip it.
     * @param start the start delimiter
     * @param end the end delimiter
     * @return the content of the block
     * @throws org.ubi.SyntaxError
     * @throws java.io.IOException
     */
    public String readBlock(String start, String end, char escapeChar) throws SyntaxError, EOFException {

        skipWhitespace();
        mark();
        if(!matches(start, true)) {
            char c = read();
            reset();
            throw new SyntaxError(getLocation(), "Trying to read block delimited by "
            		+spelled(start)+spelled(end)+", but "+spelled(c)+" found instead.");
        }

        StringBuilder output = new StringBuilder();

        int count = 1;
        char chr;

        try { reading: while(true) {

            if(matches(end, true)) {
                if(--count <= 0) {
                    break reading;
                }
            } else if(matches(start, true)) {
                ++count;
            } else {
                chr = read();
                if(chr == escapeChar) {
                    output.append(chr);
                    chr = read();
                }
                output.append(chr);
            }

        } } catch(EOFException e) {
        	// Normal operation
        }

        return output.toString();

    }

    /**
     * Throws a SyntaxError with the current location
     * @param string
     */
    public void err(String msg) throws SyntaxError {
        throw new SyntaxError(getLocation(), msg);
    }

    /**
     * Return a String representation of a character, with spelled
     * out representations of newlines, tabs, etc.
     * Example: spelled(32) = " ";
     * Example: spelled('\n') = "\\n";
     */
    public static String spelled(char character) {
        switch(character) {
        	case '\"':
        		return "\\\"";
            case '\t':
                return "\\t";
            case '\r':
                return "\\r";
            case '\n':
                return "\\n";
            case '\0':
                return "\\0";
            default:
                return Character.toString(character);
        }
    }
    
    public static void spelled(char character, Appendable output) throws IOException {
        switch(character) {
        	case '\"':
        		output.append("\\\""); return;
            case '\t':
            	output.append("\\t"); return;
            case '\r':
            	output.append("\\r"); return;
            case '\n':
            	output.append("\\n"); return;
            case '\0':
            	output.append("\\0"); return;
            default:
            	output.append(character); return;
        }
    }

    /**
     * Return a String representation of a String, with spelled
     * out representations of newlines, tabs, etc.
     * Example: spelled(32) = " ";
     * Example: spelled('\n') = "\\n";
     */
    public static String spelled(String str) {

		StringBuilder output = new StringBuilder();
		try {
			spelled(str, output);
		} catch (IOException e) {
			throw new Error(e);
		}
		return output.toString();

    }

	public static void spelled(String str, Appendable output) throws IOException {
		
		int length = str.length();
		for(int i = 0; i < length; i++) {
			spelled(str.charAt(i), output);
        }
		
	}

    /**
     * Return the String containing the whole content this SourceReader is reading from.
     */
    public char[] getContent() {
    	
        return content;
        
    }

    /**
     * Put the current index in token.start and return true.
     * Intended to be used like this:
     * <code>
     * static Token token = new Token();
     * void parse(SourceReader read) {
     *   if(reader.startToken(token) && reader.matches("myKeyword", true) && reader.endToken(token)) {
     * 	   // Add a copy of the Token to the token list.
     *   }
     * }
     * </code>
     * @param token
     * @return
     */
    public boolean startToken(Token token) {

    	token.start = index;
    	return true;
    	
    }
    
    
    /**
     * Put the current index in token.end and return true.
     * Intended to be used like this:
     * <code>
     * static Token token = new Token();
     * void parse(SourceReader read) {
     *   if(reader.startToken(token) && reader.matches("myKeyword", true) && reader.endToken(token)) {
     * 	   // Add a copy of the Token to the token list.
     *   }
     * }
     * </code>
     * @param token
     * @return
     */
    public boolean endToken(Token token) {
 
    	token.length = index - token.start;
    	return true;
    	
    }

	@SuppressWarnings("boxing")
	public String getLine(int lineNumber) throws IOException {

		int mark = mark();
		if(newlineIndices.size() > lineNumber) {
			reset(newlineIndices.get(lineNumber));
		} else {
			reset(0);
			for(int i = 1; i < lineNumber; i++) {
				readLine();
			}
		}
		
		String line = readLine();
		reset(mark);
		return line;
		
	}

	/**
	 * Get a slice of the source, specifying the start position
	 * and the length of the slice.
	 * @param start
	 * @param length
	 * @return
	 */
	public String getSlice(int start, int length) {

		return new String(content, start, length);
		
	}

}
