import io/Reader

fopen: extern func(...)

FileReader: class extends Reader {

	file: FILE*
	
	init: func(fileName: String) {
		file = fopen(fileName, "r");
		if(!file) {
			printf("[io.FileReader] File not found: %s\n", fileName);
			exit(1);
		}
	}

	read: func(chars: String, offset: Int, count: Int) {
	
	}
	
	readChar: func() -> Char {
		return 'a'
	}
	
	hasNext: func() -> Bool {
		return false
	}
	
	rewind: func(offset: Int) {
		
	}
	
	mark: func() -> Long { 
		return 0
	}
	
	reset: func(marker: Long) {
	
	}
}

main: func() {
	"Test" println()
}