include stdio, stdlib;
import io.Writer;

class FileWriter from Writer {

	FILE* file;
	
	func new(String fileName) {
		file = fopen(fileName, "w");
		if(!file) {
			printf("[io.FileWriter] File cannot be opened for writing: %s\n", fileName);
			exit(1);
		}
	}
	
	/**
	 * @param file A file to write to. Must be fopen()ed already. 
	 */
	func new(=file) {
		if(!file) {
			printf("[io.FileWriter] Bad file passed to constructor.\n");
			exit(1);
		}
	}
	
	implement flush {
		fflush(file);
	}
	
	implement closeImpl {
		fclose(file);
	}
	
	func write(Char c) {
		fputc(c, file);
	}
	
	func write(Char[] cbuf, Int off, Int len) {
		// The size of a Char is always 1
		fwrite(cbuf + off, 1, len, file);
	}

}
