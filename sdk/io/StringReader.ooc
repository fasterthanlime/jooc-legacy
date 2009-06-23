include stdio, stdlib, string;
import io.Reader;

class StringReader from Reader {

	String content;
	Long length;

	new(=content) {
		length = strlen(content);
	}
	
	implement read {
		if(marker + count < length) {
			memcpy(chars, content + marker, count);
			marker += count;
		} else {
			//FIXME should throw an exception here
		}
	}
	
	implement readChar {
		if(hasNext) {
			return content[marker++];
		} else {
			return 0;
		}
	}
	
	implement hasNext {
		return marker < length;
	}
	
	implement rewind {
		marker -= offset;
	}
	
	implement mark {
		return marker;
	}
	
	implement reset {
		this.marker = marker;
	}

}
