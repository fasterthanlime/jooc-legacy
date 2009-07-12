//#define _BSD_SOURCE

include stdio, stdlib, sys/types, sys/stat, unistd, dirent, sys/dir;

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
	
	static Int CAN_READ  = 0x100; //4
  	static Int CAN_WRITE = 0x010; //2
  	static Int CAN_EXEC  = 0x001; //1
	
	func new(=path);
	
	func children -> List {
		
		List list = (List) new ArrayList;
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
		return S_ISDIR(stat.st_mode);	
	}

	func isFile -> Bool {

		FileStat stat;
		lstat(path, &stat);
		return S_ISREG(stat.st_mode);
	}

	func isLink -> Bool {

		FileStat stat;
		lstat(path, &stat);	
		return S_ISLNK(stat.st_mode);
	}

	func stSize -> Int {

		FileStat stat;
		lstat(path, &stat);	
		return (stat.st_size);
	}

	static func stSize (String s) -> Int {

		FileStat stat;
		lstat(s, &stat);	
		return (stat.st_size);
	}

	func exist -> Bool {
				
		if (!fopen(path, "r")){
		//printf("No file or such directory.\n");
		return (fopen(path, "r"));
		}
		else{
		return (fopen(path, "r"));
		}	
	}

	static func exist (String s) -> Bool {
				
		if (!fopen(s, "r")){
		//printf("No file or such directory.\n");
		return (fopen(s, "r"));
		}
		else{
		return (fopen(s, "r"));
		}				
	}

	func comparePath (String s) -> Bool {
	
		if (File.exist(s) && File.exist(path)){
		return true;
		}
		else{
		return false;
		}
	}
	//func create (String s)
	
	//static func createDir (String s)
	
	func ownerPerm -> Int {

		FileStat stat;
  		lstat(path, &stat);
  		return (stat.st_mode & S_IRWXU);
	}

	func groupPerm -> Int {
	
		FileStat stat;
  		lstat(path, &stat);
 		return (stat.st_mode & S_IRWXG);
	}

	func otherPerm -> Int {

		FileStat stat;
 		lstat(path, &stat);
  		return (stat.st_mode & S_IRWXO);
	}

	func name -> String {
		
		Int idx = path.lastIndexOf('/');
		if(idx == -1) {
			return path;
		} else {
			return path.substring(idx + 1);
		}
		
	}
	
}
