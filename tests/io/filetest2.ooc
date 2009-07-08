include stdio, stdlib, sys/types, sys/stat, unistd, dirent;

import lang.String;
import structs.List, structs.ArrayList;

ctype DIR, dirent, stat;
typedef DIR* Dir;
typedef struct dirent DirEntry;
typedef struct stat FileStat;

	


func main (Int argc, Char ** argv) {

	

	String path  = "/etc";
	FileStat stat;
	
	lstat(path, &stat);
       
	if(stat.st_mode & S_IWOTH) {
        printf("Great! we can launch gcc =)\n");
       	
	}else{

	printf("Sorry! we can't launch gcc =)\n");
	}
}
