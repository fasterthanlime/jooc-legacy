include stdio, sys/types, dirent;

import lang.String;
import structs.List, structs.ArrayList;

ctype DIR;
ctype dirent;
typedef DIR* Dir;
typedef struct dirent* DirEntry;

class File {

	String path;
	version(unix) {
		static String separator = "/";
	}
	version(windows) {
		static String separator = "\\";
	}
	
	func new(=path);
	
	func children -> List {
		
		List list = new ArrayList;
		Dir dp;
		DirEntry ep;
     
		dp = opendir (path);
		if(dp != null) {
			
			while (ep = readdir(dp)) {
				list.add(new File(ep->d_name));
			}
			
			closedir (dp);
		   
        } else {
			
			fprintf(stderr, "[%s] Couldn't open the directory", class.name);
			
		}
     
		return new ArrayList;
		
	}
	
	func isDir -> Bool {
		
		return false;
		
	}
	
	func name -> String {
		
		return path.substring(path.indexOf("/"));
		
	}
	
}
