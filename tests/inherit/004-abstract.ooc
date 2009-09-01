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

Cat: class extends Animal {
	// comment the next line to test the "must implement all abstract methods" error message
	setThing: func {}
}

main: func {

	dog := Dog new()
	dog setThing()
	dog getThing()

}
