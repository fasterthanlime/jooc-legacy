import lang.String;
include string;

class StringTokenizer {

	String str;
	String delim;
	Int index, length;

	new(=str, =delim) {
		
		length = str.length;
		index = 0;
		
	}

	func hasNext -> Bool {
		
		return index < str.length;
		
	}

	func nextToken(String delim) -> String {
		
		String buffer = malloc(length + 1); // chars are always of size 1
		Int bufIndex = 0;
		Int delimLength = delim.length;
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
	
	func nextToken -> String {
		return nextToken(delim);
	}

}
