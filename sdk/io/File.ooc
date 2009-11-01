/**
 * Allows to test various file attributes, list the children
 * of a directory, etc.
 * 
 * @author Pierre-Alexandre Croiset
 * @author fredreichbier
 * @author Amos Wenger
 */

// the pipe (e.g. '|') and __USE_BSD are used like #define
// before includes. In this case, we need __USE_BSD to get lstat()
include sys/types, sys/stat | (__USE_BSD)
include stdio

import structs/ArrayList

import FileReader, FileWriter
import dirent

include unistd
getcwd: extern func(buf: String, size: SizeT) -> String

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
    PATH_MAX := static const 16383 // cause we alloc +1
    
	path: String
	separator = '/' : static const Char
	pathDelimiter = ':' : static const Char
		
	getPath: func -> String {
		return path
	}

	init: func(=path) {}
    
    init: func ~parentFile(parent: File, .path) { this(parent path + File separator + path) }
    
    init: func ~parentPath(parent: String, .path) { this(parent + File separator + path) }
	
    /**
     * @return true if it's a directory
     */
	isDir: func -> Bool {
		stat: FileStat
		lstat(path, stat&)
		return S_ISDIR(stat st_mode)
	}
	
    /**
     * @return true if it's a file (ie. not a directory)
     */
	isFile: func -> Bool {
		stat: FileStat
		lstat(path, stat&);
		return S_ISREG(stat st_mode);
	}
	
    /**
     * @return true if the file is a symbolic link
     */
	isLink: func -> Bool {
		stat: FileStat
		lstat(path, stat&);
		return S_ISLNK(stat st_mode);
	}
	
    /**
     * @return the size of the file, in bytes
     */
	size: func -> Int {
		stat: FileStat
		lstat(path, stat&);
		return stat st_size;
	}
	
    /**
     * @return true if the file exists and can be
     * opened for reading
     */
	exists: func -> Bool {
		return fopen(path, "r") ? true : false
	}
	
    /**
     * @return the permissions for the owner of this file
     */
	ownerPerm: func -> Int {
		stat: FileStat
		lstat(path, stat&);
		return (stat st_mode) & S_IRWXU;
	}
	
    /**
     * @return the permissions for the group of this file
     */
	groupPerm: func -> Int {
		stat: FileStat
		lstat(path, stat&);
		return (stat st_mode) & S_IRWXG;
	}
	
    /**
     * @return the permissions for the others (not owner, not group)
     */
	otherPerm: func -> Int {
		stat: FileStat
		lstat(path, stat&);
		return (stat st_mode) & S_IRWXO;
	}
	
    /**
     * @return the last part of the path, e.g. for /etc/init.d/bluetooth
     * name() will return 'bluetooth'
     */
	name: func -> String {
		trimmed := path trim(separator)
		idx := trimmed lastIndexOf(separator)
		if(idx == -1) return trimmed
		return trimmed substring(idx + 1)
	}
    
    /**
     * @return the parent of this file, e.g. for /etc/init.d/bluetooth
     * it will return /etc/init.d/ (as a File), or null if it's the
     * root directory.
     */
    parent: func -> File {
        pName := parentName()
        if(pName) return File new(pName)
        return null
    }
    
    /**
     * @return the parent of this file, e.g. for /etc/init.d/bluetooth
     * it will return /etc/init.d/ (as a File), or null if it's the
     * root directory.
     */
    parentName: func -> String {
        idx := path lastIndexOf(separator)
        if(idx == -1) return null
        return path substring(0, idx)
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
    
    copyTo: func(dstFile: This) {
        dstFile parent() mkdirs()
        src := FileReader new(this)
        dst := FileWriter new(dstFile)
        max := 8192
        buffer : Char[max]
        while(src hasNext()) {
            num := src read(buffer, 0, max)
            dst write(buffer, num)
        }
        dst close()
        src close()
    }
    
    getChildrenNames: func -> ArrayList<String> {
        // TODO: check if we're a dir
        // TODO: check if dir is null
        dir := opendir(path)
        result := ArrayList<String> new()
        entry := readdir(dir)
        while(entry != null) {
            result add(entry@ name clone())
            entry = readdir(dir)
        }
        closedir(dir)
        return result
    }
    
    getChildren: func -> ArrayList<This> {
        // TODO: check if we're a dir
        // TODO: check if dir is null
        dir := opendir(path)
        result := ArrayList<This> new()
        entry := readdir(dir)
        while(entry != null) {
            result add(File new(entry@ name clone()))
            entry = readdir(dir)
        }
        closedir(dir)
        return result
    }

    getChild: func (name: String) -> This {
        new(this path + File separator + name)
    }
    
    /**
     * @return the current working directory
     */
	getCwd: static func() -> String {
		ret := String new(File PATH_MAX + 1)
		getcwd(ret, File PATH_MAX)
        return ret
	}
    
}
