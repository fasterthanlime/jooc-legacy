Funky: class {

	new: func {
		printf("Created a Funky =)\n")
	}

	/*
	getOne: static func -> This {
		new This
	}
	*/

}

main: func {

	f = new Funky() : Funky // Fully explicit
	f2 = new Funky : Funky // No-parenthesis
	f3 = new : Funky // VariableDeclAssigned
	f4 : Funky
	f4 = new // Assignment
	f5 := new Funky
	f6 := new as Funky // mwahahaha.
	//f7 := Funky getOne
	
}
