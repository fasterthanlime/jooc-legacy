main: func {

	new IntContainer()

}

IntContainer: class {

	value: Int

	new: func {
		value = 42
		this print()
	}

	print: func {
		printf("The answer is %d\n", value)
	}

}