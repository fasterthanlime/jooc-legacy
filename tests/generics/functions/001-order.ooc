size: func <B, A> (arg1: B, arg2: A) {
	printf("1st size = %d\n", B size)
	printf("2nd size = %d\n", A size)
}

size2: func <A, B> (arg1: B, arg2: A) {
	printf("1st size = %d\n", B size)
	printf("2nd size = %d\n", A size)
}

size3: func <B, A> (arg1: A, arg2: B) {
	printf("1st size = %d\n", A size)
	printf("2nd size = %d\n", B size)
}

size4: func <A, B> (arg1: A, arg2: B) {
	printf("1st size = %d\n", A size)
	printf("2nd size = %d\n", B size)
}

main: func {
	"Calling size()" println()
	size('c', "bouh")
	"Calling size2()" println()
	size2('c', "bouh")
	"Calling size3()" println()
	size3('c', "bouh")
	"Calling size4()" println()
	size4('c', "bouh")
}
