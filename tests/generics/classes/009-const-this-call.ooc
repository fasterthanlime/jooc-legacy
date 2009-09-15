Container: class <T> {

	field: Int

	init: func ~dumbSuffix {
		//init ~otherDumbSuffix (42)
		this(42)
	}
	
	init: func ~otherDumbSuffix (=field) {
		printf("Inited field to %d\n", field)
	}

}

main: func {

	Container<Int> new()
	Container<Float> new(3)

}
