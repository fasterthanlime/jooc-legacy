include sys/stat

FileStat: cover from struct stat
S_ISDIR: extern func(...) -> Bool
S_ISREG: extern func(...) -> Bool
lstat: extern func(String, FileStat*) -> Int

File: class {
	path: String
	separator = getSystemSeparator() : static const Char
	
	getSystemSeparator: static func -> Char {
		/*version(unix) {
			return '/'
		}
		version(windows) {
			return '\\'
		}*/
		
		return '/'
	}

	init: func(=path) { }
	
	isDir: func -> Bool {
		stat: FileStat
		lstat(path, stat&)
		return S_ISDIR(stat st_mode)
	}
	
	isFile: func -> Bool {
		stat: FileStat
		lstat(path, stat&);
		return S_ISREG(stat st_mode);
	}
	
	isLink: func -> Bool {
		return false
	}
	
	stSize: func -> Int {
		return 0
	}
	
	exists: func -> Bool {
		return false
	}
	
	ownerPerm: func -> Int {
		return 0
	}
	
	groupPerm: func -> Int {
		return 0
	}
	
	otherPerm: func -> Int {
		return 0
	}
	
	name: func -> String {
		return ""
	}
}

main: func {
	file := File new("sdk/io/File.ooc")
	dir := File new("sdk/io")
}
