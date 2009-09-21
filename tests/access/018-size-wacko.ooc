Wacko: class {

	size = 0 : Int

	init: func {
		printf("My size is %d\n", size)
	}

}

main: func {

	printf("Size of a wacko = %d\n", Wacko size)
	Wacko new()
	printf("Size of a wacko is now %d\n", Wacko size)

}
