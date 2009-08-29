main: func {
	print(d := 42 class)
	print(d := 3.14 class)
}

print: func ~class (c: Class) {
	printf("Class name: %s\n", c name)
}
