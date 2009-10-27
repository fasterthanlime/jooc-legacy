import io/File
import structs/ArrayList 

import dirent

include unistd
getcwd: extern func(buf: String, size: SizeT) -> String

Directory: class {
    dir: DirPtr
    dirPath: String

    init: func (=dirPath) {
        dir = opendir(dirPath)
        /* TODO: check if dir is null */
    }

    destroy: func {
        closedir(dir)
    }

    getEntries: func -> ArrayList<String> {
        result := ArrayList<String> new()
        entry := readdir(dir)
        while(entry != null) {
            result add(entry@ name clone())
            entry = readdir(dir)
        }
        return result
    }

    getFileNames: func -> ArrayList<String> {
        /* TODO: we don't really need two arrays -> make it more efficient */
        result := ArrayList<String> new()
        entries := getEntries()
        for(entry: String in entries) {
            path := dirPath + File separator + entry
            stat: FileStat
            lstat(path, stat&)
            if(S_ISREG(stat st_mode)) {
                result add(entry) // no need to copy the string here
            }
        }
        return result
    }
    
    getDirectoryNames: func -> ArrayList<String> {
        /* TODO: we don't really need two arrays -> make it more efficient */
        result := ArrayList<String> new()
        entries := getEntries()
        for(entry: String in entries) {
            path := dirPath + File separator + entry
            stat: FileStat
            lstat(path, stat&)
            if(S_ISDIR(stat st_mode) && !entry equals(".") && !entry equals("..")) {
                result add(entry) // no need to copy the string here
            }
        }
        return result
    }

    getFiles: func -> ArrayList<File> {
        result := ArrayList<File> new()
        paths := getFileNames()
        for(path: String in paths) {
            result add(getFile(path))
        }
        return result
    }

    getDirectories: func -> ArrayList<File> {
        result := ArrayList<File> new()
        paths := getDirectoryNames()
        for(path: String in paths) {
            result add(getDirectory(path))
        }
        return result
    }

    getDirectory: func (name: String) -> Directory {
        Directory new(this dirPath + File separator + name)
    }

    getFile: func (name: String) -> File {
        File new(this dirPath + File separator + name)
    }

	getCwd: static func() -> String {
		ret := String new(File PATH_MAX + 1)
		getcwd(ret, File PATH_MAX)
        return ret
	}
}

errno: Int
