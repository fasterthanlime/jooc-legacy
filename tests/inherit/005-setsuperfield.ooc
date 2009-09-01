Animal: abstract class {

	name: String

}

Dog: class extends Animal {

	init: func(=name) {}

}

main: func {

	printf("The dog's name is %s\n", Dog new("Fido") name)


}
