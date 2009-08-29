include stdio, stdlib;
import io.Reader;

class FileReader from Reader {

	FILE* file;
	
	func new(String fileName) {
		file = fopen(fileName, "r");
		if(!file) {
			printf("[io.FileReader] File not found: %s\n", fileName);
			exit(1);
		}
	}
	
	/**
	 * @param file A file to read from. Must be fopen()ed already. 
	 */
	func new(=file) {
		if(!file) {
			printf("[io.FileReader] Bad file passed to constructor.\n");
			exit(1);
		}
	}
	
	implement read {
		fread(chars + offset, 1, count, file);
	}

	implement readChar {
		Char value;
		fread(&value, 1, 1, file);
		return value;
	}
	
	implement hasNext {
		return !feof(file);
	}
	
	implement rewind {
		fseek(file, -offset, SEEK_CUR);
	}
	
	implement mark {
		marker = ftell(file);
		return marker;
	}
	
	implement reset {
		fseek(file, marker, SEEK_SET);
	}

}
