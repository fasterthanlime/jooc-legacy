main: func {

	IntContainer new(42)

}

IntContainer: class {

	value: Int

	init: func(.value) {
		this value = value
		print()
	}

	print: func {
		printf("The answer is %d\n", value)
	}

}
