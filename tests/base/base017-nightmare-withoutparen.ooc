Thinga: class {

	count: Int

	getThis: func -> This {
		this count = this count + 1
		this
	}

	thingo: func {
		printf("Thingo bingo =) count = %d\n", this count)
	}

}

main: func {

	thinga := Thinga new()
	Thinga new() getThis() getThis() getThis() getThis() thingo()

}
