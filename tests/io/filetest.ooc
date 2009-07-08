include stdio, stdlib, sys/types, sys/stat, unistd, dirent;

import lang.String;
import structs.List, structs.ArrayList;

ctype DIR, dirent, stat;
typedef DIR* Dir;
typedef struct dirent DirEntry;
typedef struct stat FileStat;

class File {
	String path;
	
	static Int CAN_READ  = 0x100; // 4
  	static Int CAN_WRITE = 0x010; // 2
  	static Int CAN_EXEC  = 0x001; // 1

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

	func name -> String {
		
		Int idx = path.lastIndexOf('/');
		if(idx == -1) {
			return path;
		} else {
			return path.substring(idx + 1);
		}
		
	}

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
}


	func main (Int argc, Char ** argv) {

	
	File f = new File("/usr/bin/gcc");
       
	if(f.ownerPerm & File.CAN_READ) {
        printf("Great! we can launch gcc =)\n");
       	
	}else {

	printf("Sorry! we can't launch gcc =)\n");
}
	
}























