// the pipe (e.g. '|') and __USE_BSD are used like #define
// before includes. In this case, we need __USE_BSD to get lstat()
include sys/types, sys/stat | (__USE_BSD)

ModeT: cover from mode_t
FileStat: cover from struct stat {
	st_mode: extern ModeT
}


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
		trimmed := path trim(separator)
		idx := trimmed lastIndexOf(separator)
		if(idx == -1) return trimmed
		return trimmed substring(idx + 1)
	}
}

main: func {
	file := File new("/bin/ls")
	dir := File new("/bin/")
	printf("%s\t(name = %s)\tisFile? %s\tisDir? %s\n", file path, file name(), file isFile() repr(), file isDir() repr())
	printf("%s\t(name = %s)\tisFile? %s\tisDir? %s\n", dir path, dir name(), dir isFile() repr(), dir isDir() repr())
}
