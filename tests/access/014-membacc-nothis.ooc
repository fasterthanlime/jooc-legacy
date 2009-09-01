main: func {

	Dog new("Dogbert") print()

}

Animal: class {

	name: String
	init: func(=name) {}

}

Dog: class extends Animal {

	init: func(.name) { super(name) }

	print: func {
		printf("My name is %s\n", name)
	}

}
