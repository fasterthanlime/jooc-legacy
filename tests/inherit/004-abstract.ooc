Animal: abstract class {

	setThing: abstract func {}
	getThing: func -> Int { 42 }

}

Dog: class extends Animal {

	setThing: func {
		printf("Setting thing!\n")
	}

	getThing: func {
		printf("Getting thing..\n")
	}

}

//Cat: class extends Animal {}

main: func {

	dog := Dog new()
	dog setThing()
	dog getThing()

}
