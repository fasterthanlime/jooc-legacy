include stdio, stdlib, sys/types, sys/stat, unistd;

import lang.String;

ctype DIR, dirent, stat;
typedef struct stat FileStat;

func main(Int argc, String[] argv) {

	String path  = "/usr/bin/gcc";
	FileStat stat;
	lstat(path, &stat);
       
	if(stat.st_mode & S_IXOTH) {
		printf("Great! we can launch gcc =)\n");
	} else {
		printf("Sorry! we can't launch gcc =)\n");
	}
}
