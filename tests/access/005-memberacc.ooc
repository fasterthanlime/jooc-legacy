main: func {

	cont = new : IntContainer
	cont value = 42
	if (true) printf("The answer is %d\n", cont value)

}

IntContainer: class {

	value: Int

}
