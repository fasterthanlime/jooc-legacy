Dog: class {
	bloh: func {}
}

Hound: class extends Dog {
	i := 42
}

main: func {
	printType(Dog)
	printType(Hound)
	printType('c' class)
	printType(42 class)
	printType(3.14 class)
	printType(8.0 as Double class)
	printType(6.52 as LDouble class)
}

printType: func (c: Class) {
	printf("Class hierarchy = ")
	while (c) {
		mess := "bytes"
		if(c size == 1) mess = "byte"
		printf("%s (%d %s)", c name, c size, mess)
		c = c super
		if(c) printf(" -> ")
	}
	printf("\n-----------------\n");
}

