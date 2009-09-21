import io/Reader

fopen: extern func(filename: Char*, mode: Char*) -> FILE*

FileReader: class extends Reader {

	file: FILE*
	
	init: func(fileName: String) {
		file = fopen(fileName, "r");
		if (!file) 
			Exception new("File not found: " + fileName) throw()
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
	fr := FileReader new("asdf") 
}