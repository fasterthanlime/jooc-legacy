import lang.String, io.File;

func main(Int argc, String[] argv) {
	
	File f = new File("/usr/bin/gcc");
       
	if(f.otherPerm & File.CAN_EXEC) {
        printf("Great! we can launch gcc =)\n");
	} else {
		printf("Sorry! we can't launch gcc =)\n");
	}
	
}























