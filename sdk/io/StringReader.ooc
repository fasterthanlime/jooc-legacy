include stdio, stdlib, string;
import io.Reader;

class StringReader from Reader {

	String content;
	Long length;

	new(=content) {
		length = strlen(content);
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
