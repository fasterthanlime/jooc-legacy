import io/File

main: func {
	
	file := File new("/bin/ls") as File
	dir := File new("/bin/") as File
	
	printf("%s\t(name = %s)\tisFile? %s\tisDir? %s\tsize: %d\n",
                file path, file name(), file isFile() toString(), file isDir() toString(), file size())
	printf("%s\t(name = %s)\tisFile? %s\tisDir? %s\tsize: %d\n",
                dir path, dir name(), dir isFile() toString(), dir isDir() toString(), dir size())
	
	asdf := File new("asdf") as File
	printf("%s exists? %s\n", asdf name(), asdf exists() toString())
	printf("%s exists? %s\n", file name(), file exists() toString())
	
}
