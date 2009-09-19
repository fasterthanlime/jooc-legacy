Animal: abstract class {
	
	shout: abstract func
	
}

Dog: class extends Animal {

	shout: func {
		printf("Woof, woof!\n")
	}
	
}


main: func {
	
	dog := Dog new()
	animal := dog as Animal
	printf("Address of dog shout = %p\n", dog shout)
	printf("Address of dog as Animal shout = %p\n", animal shout)
	dog shout()
	animal shout()
	
}
