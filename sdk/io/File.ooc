//#define _BSD_SOURCE

include stdio, stdlib, sys/types, sys/stat, unistd, dirent;

import lang.String;
import structs.List, structs.ArrayList;

ctype DIR, dirent, stat;
typedef DIR* Dir;
typedef struct dirent DirEntry;
typedef struct stat FileStat;

class File {

	String path;
	static Char separator = '/';
	
	static {
		version(unix) {
			separator = '/';
		}
		version(windows) {
			separator = '\\';
		}
	}
	
	func new(=path);
	
	func children -> List {
		
		List list = new ArrayList;
		Dir dp;
		DirEntry* ep;
     
		dp = opendir (path);
		if(dp != null) {
			
			while (ep = readdir(dp)) {
				String dName = ep->d_name;
				if(!dName.equals(".") && !dName.equals("..")) {
					String fullPath = malloc(path.length + 2 + dName.length);
					memcpy(fullPath, path, path.length);
					fullPath[path.length] = separator;
					memcpy(fullPath + path.length + 1, dName, dName.length);
					
					list.add(new File(fullPath));
				}
			}
			
			closedir (dp);
		   
        } else {
			
			fprintf(stderr, "[%s] Couldn't open the directory", class.name);
			
		}
     
		return list;
		
	}
	
	func isDir -> Bool {
		
		FileStat stat;
		lstat(path, &stat);
		return (stat.st_mode & S_IFDIR);
		
	}
	
	func name -> String {
		
		return path.substring(path.indexOf("/"));
		
	}
	
}
