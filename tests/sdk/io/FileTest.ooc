import io/File

main: func {
	
	file := File new("/bin/ls") as File
	dir := File new("/bin/") as File
	
	printf("%s\t(name = %s)\tisFile? %s\tisDir? %s\tsize: %d\n", file path, file name(), file isFile() repr(), file isDir() repr(), file size())
	printf("%s\t(name = %s)\tisFile? %s\tisDir? %s\tsize: %d\n", dir path, dir name(), dir isFile() repr(), dir isDir() repr(), dir size())
	
	asdf := File new("asdf") as File
	printf("%s exists? %s\n", asdf name(), asdf exists() repr())
	printf("%s exists? %s\n", file name(), file exists() repr())
	
}
