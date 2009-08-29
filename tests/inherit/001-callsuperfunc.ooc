main: func {
	fido := new Dog("Fido")
	fido sayName()
	fido bark()
}

Animal: class {

	name: String;
	sayName: func {
		printf("Hi, my name is %s\n", name)
	}

}

Dog: class extends Animal {

	new: func (=name)
	
	bark: func {
		printf("Woof, woof!\n")
	}

}
