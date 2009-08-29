Thinga: class {

	count: Int

	getThis: func -> This {
		this count = this count + 1
		return this
	}

	thingo: func {
		printf("Thingo bingo =) count = %d\n", this count)
	}

}

main: func {

	thinga = new Thinga() : Thinga
	new Thinga() getThis() getThis() getThis() getThis() thingo()

}
