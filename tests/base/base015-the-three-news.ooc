Funky: class {

	init: func {
		printf("Created a Funky =)\n")
	}

	getOne: static func -> This {
		This new()
	}

}

main: func {

	f = Funky new() : Funky
	f2 := Funky new()
	
}
