Dog: class {
	bloh: func{}
}

Hound: class extends Dog {
	i := 42
}

main: func {
	printType(d := Dog new())
	printType(h := Hound new())
	printType(c := 'c')
	printType(i := 42)
	printType(f := 3.14)
	printType(d := 8.0 as Double)
	printType(ld := 6.52 as LDouble)
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
