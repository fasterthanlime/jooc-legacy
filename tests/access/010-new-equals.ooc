main: func {

	new IntContainer(42)

}

IntContainer: class {

	value: Int

	new: func(=value) {
		print()
	}

	print: func {
		printf("The answer is %d\n", value)
	}

}
