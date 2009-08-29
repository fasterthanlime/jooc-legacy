
Dog: class {

	name: String | private, get, set
	
	new: func(=name)
	
	println: func {
		printf("I'm a dog named %d\n", name);
	}

}

main: func {
	
	//dogbert := new Dog("Dogbert")
	dogbert := new Dog
	dogbert setName("Dogbert")
	dogbert name = "Dogbert"
	
	dogbert name println()
	dogbert getName() println()
	
}
