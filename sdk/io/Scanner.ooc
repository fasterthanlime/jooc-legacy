include stdio, stdlib;

import io.Reader;

/**
 * Utility high-level class to allow easy reading of data from a reader
 * (ie. file, string, etc.)
 */
class Scanner {

	const static Int BUFFER_SIZE = 4096;
	
	Reader reader;
	String buffer;

	/** 
	 * @param reader A reader.
	 */
	func new(=reader) {
		buffer = malloc(BUFFER_SIZE);
	}
	
	/**
	 * Delegate for Reader.readChar();
	 */
	func readChar -> Char {
		return reader.readChar();
	}
	
	/**
	 * Delegate for Reader.mark();
	 */
	func mark -> Long {
		return reader.mark();
	}
	
	/**
	 * Delegate for Reader.rewind();
	 */
	func rewind(Int offset) {
		reader.rewind(offset);
	}

	/**
	 * Read an float from the reader and return it.
	 * @return the read float
	 */
	func readFloat -> Float {
		Int index = 0;
		Bool gotDot = false;
		while(reader.hasNext) {
			buffer[index] = reader.readChar;
			if((buffer[index] < '0' || buffer[index] > '9') && buffer[index] != '.') {
				break;
			}
			if(buffer[index] == '.') {
				if(gotDot) {
					break;
				} else {
					gotDot = true;
				}
			}
			index++;
			if(index >= BUFFER_SIZE) {
				printf("Error: buffer overflow (trying to read more than %d (buffer size) chars long !), abandoning..\n", BUFFER_SIZE);
				exit(1);
			}
		}
		if(!reader.hasNext) {
			if(index == 0) {
				return -1;
			}
		} else if(buffer[index - 1] != 'f') {
			reader.rewind(1); // We read one too many
		}
		buffer[index] = '\0';
		return atof(buffer);
	}

	/**
	 * Read an Int from the reader and return it.
	 * @return the read Int
	 */
	func readInt -> Int {
		Int index = 0;
		while(reader.hasNext) {
			buffer[index] = reader.readChar;
			if(buffer[index] < '0' || buffer[index] > '9') {
				break;
			}
			index++;
			if(index >= BUFFER_SIZE) {
				printf("Error: buffer overflow (trying to read more than %d (buffer size) chars long !), abandoning..\n", BUFFER_SIZE);
				exit(1);
			}
		}
		if(!reader.hasNext) {
			if(index == 0) {
				return -1;
			}
		} else {
			reader.rewind(1); // We read one too many
		}
		buffer[index] = '\0';
		return atoi(buffer);
	}

	/**
	 * Read until we reach 'c'
	 */
	func readUntil(Char c, Bool keepEnd) -> String {
		Int index = 0;
		while(reader.hasNext) {
			buffer[index] = reader.readChar;
			if(buffer[index] == c) {
				break;
			}
			index++;
			if(index >= BUFFER_SIZE) {
				printf("Error: buffer overflow (trying to read more than %d (buffer size) chars long !), abandoning..\n", BUFFER_SIZE);
				exit(1);
			}
		}
		if(!reader.hasNext) {
			if(index == 0) {
				return "";
			}
			index--;
		} else if(!keepEnd) {
			reader.rewind(1);
		}
		buffer[index] = '\0';
		return buffer;
	}

	/**
	 * Read until we reach '\n'
	 */
	func readLine -> String {
		return readUntil('\n', true);
	}

	/**
	 * Skip all whitespace ('\t', ' ', '\r', '\n').
	 */
	func skipWhitespace {
		Char c;
		while(reader.hasNext) {
			c = reader.readChar;
			if(c != '\t' && c != ' ' && c != '\r' && c != '\n') {
				break;
			}
		}
		if(reader.hasNext) {
			reader.rewind(1); // We read one too much
		}
	}
	
	/**
     * Test if a "candidate" matches the next characters in the content.
     * @param candidate
     * @param keepEnd If false, will reset to the initial position before returning.
     * If true, will stay after the matched candidate.
     * @return true if the candidate matches, false otherwise.
     */
    func matches(String candidate, Bool keepEnd) -> Bool {

        Int mark = reader.mark();
        Int i = 0;
        Int c, c2;
        Bool result = true;
        while(i < strlen(candidate)) {
            c = reader.readChar;
            c2 = candidate[i];
            if(c2 != c) {
				result = false;
				break;
            }
            i++;
        }
        if(!result || !keepEnd) {
            reader.reset(mark);
        }

        return result;

    }

	/**
	 * @return true if we're not at the end of the reader.
	 */
	func hasNext -> Bool {
		return reader.hasNext;
	}

}
