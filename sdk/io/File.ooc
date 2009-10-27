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
mkdir: extern func(String, ModeT) -> Int

PATH_MAX := const 8192
realpath: extern func(path: String, resolved: String) -> String

version(unix) {
    File separator = '/'
	File pathDelimiter = ':'
}
version(windows) {
    File separator = '\\'
	File pathDelimiter = ';'
}

File: class {
	path: String
	separator = '/' : static const Char
	pathDelimiter = ':' : static const Char
	
	PATH_MAX = PATH_MAX : static const Int
		
	getPath: func -> String {
		return path
	}

	init: func(=path) {}
	
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
	
	size: func -> Int {
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
    
    parent: func -> File {
        idx := path lastIndexOf(separator)
        if(idx == -1) return null
        return File new(path substring(0, idx))
    }
    
    mkdir: func -> Int {
        mkdir(0c755)
    }
    
    mkdir: func ~withMode (mode: Int32) -> Int {
        return mkdir(path, mode)
    }
    
    mkdirs: func {
        mkdirs(0c755)
    }
        
    mkdirs: func ~withMode (mode: Int32) -> Int {
        if(parent := parent()) {
            parent mkdirs()
        }
        mkdir()
    }
	
	getAbsolutePath: func -> String {
		// TODO, realpath() is a posix thing, needs to be versioned out
		actualPath := String new(PATH_MAX + 1)
		return realpath(path, actualPath)
	}
}
