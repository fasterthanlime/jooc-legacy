// the pipe (e.g. '|') and __USE_BSD are used like #define
// before includes. In this case, we need __USE_BSD to get lstat()
include sys/types, sys/stat | (__USE_BSD)
include stdio

ModeT: cover from mode_t
FileStat: cover from struct stat {
	st_mode: extern ModeT
	st_size: extern SizeT
}

S_ISDIR: extern func(...) -> Bool
S_ISREG: extern func(...) -> Bool
S_ISLNK: extern func(...) -> Bool
S_IRWXU: extern func(...)
S_IRWXG: extern func(...)
S_IRWXO: extern func(...)
lstat: extern func(String, FileStat*) -> Int

fopen: extern func(String, String) -> Pointer

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
		stat: FileStat
		lstat(path, stat&);
		return S_ISLNK(stat st_mode);
	}
	
	stSize: func -> Int {
		stat: FileStat
		lstat(path, stat&);
		return stat st_size;
	}
	
	exists: func -> Bool {
		if (fopen(path, "r"))
			return true
		
		return false
	}
	
	ownerPerm: func -> Int {
		stat: FileStat
		lstat(path, stat&);
		return (stat st_mode) & S_IRWXU;
	}
	
	groupPerm: func -> Int {
		stat: FileStat
		lstat(path, stat&);
		return (stat st_mode) & S_IRWXG;
	}
	
	otherPerm: func -> Int {
		stat: FileStat
		lstat(path, stat&);
		return (stat st_mode) & S_IRWXO;
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
	
	printf("%s\t(name = %s)\tisFile? %s\tisDir? %s\tsize: %d\n", file path, file name(), file isFile() repr(), file isDir() repr(), file stSize())
	printf("%s\t(name = %s)\tisFile? %s\tisDir? %s\tsize: %d\n", dir path, dir name(), dir isFile() repr(), dir isDir() repr(), dir stSize())
	
	asdf := File new("asdf")
	printf("%s exists? %s\n", asdf name(), asdf exists() repr())
	printf("%s exists? %s\n", file name(), file exists() repr())
	
}