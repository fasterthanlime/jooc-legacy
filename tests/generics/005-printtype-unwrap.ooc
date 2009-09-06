Dog: class {
	bloh: func {}
}

Hound: class extends Dog {
	i := 42
}

main: func {
	printType(Dog new())
	printType(Hound new())
	printType('c')
	printType(42)
	printType(3.14)
	printType(8.0 as Double)
	printType(6.52 as LDouble)
}

printType: func <T> (arg: T) {
	printf("Class hierarchy = ")
	c := T
	while (c) {
		mess := "bytes"
		if(c size == 1) mess = "byte"
		printf("%s (%d %s)", c name, c size, mess)
		c = c super
		if(c) printf(" -> ")
	}
	printf("\n")
	if(T == Char class) {
		printf("Value = %c\n", arg as Char)
	} else if(T == Int class) {
		printf("Value = %.2d\n", arg as Int)
	} else if(T == Double class) {
		printf("Value = %.2f\n", arg as Double)
	} else if(T == LDouble class) {
		printf("Value = %.2Lf\n", arg as LDouble)
	} else if(T == Float class) {
		printf("Value = %.2f\n", arg as Float)
	}
	printf("-----------------\n")
}
