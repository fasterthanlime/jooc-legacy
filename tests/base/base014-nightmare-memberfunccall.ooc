Thinga: class {

	count: Int

	getThis : func -> This {
		count += 1
		this
	}

	thingo: func {
		printf("Thingo bingo =) count = %d\n", count)
	}

}

main: func {

	new Thinga getThis() getThis() getThis() getThis() thingo()

}
