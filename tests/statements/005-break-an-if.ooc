main: func {

	condition := true

	println("Entering the if")
	if(true) {
		println("Hi, world, we'll move on from here")
		if (condition) break
		println("This'll never be printed")
	}
	println("And now it's done")

}
