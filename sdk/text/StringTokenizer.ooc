include string;

class StringTokenizer {

	String str;
	String delim;
	Int index, length, delimLength;

	new(=str, =delim) {
		
		length = strlen(str);
		delimLength = strlen(delim);
		index = 0;
		
	}

	func hasNext -> Bool {
		
		return index < strlen(str);
		
	}

	func nextToken -> String {
		
		String buffer = malloc(length + 1); // chars are always of size 1
		Int bufIndex = 0;
		while(index < length) {
			buffer[bufIndex] = str[index++];
			for(Int i: 0..delimLength) {
				if(buffer[bufIndex] == delim[i]) {
					goto done;
				}
			}
			bufIndex++;
		}
		done:
		buffer[bufIndex] = '\0';
		return buffer;
		
	}

}
