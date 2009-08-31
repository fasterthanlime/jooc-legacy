Dog: class {

	name = "Fido" : String

	/* The compiler automatically creates this function:
	new: static func -> This {
		this := This class alloc() as This
		init()
		return this
	}
	*/

	init: func {
		name = "Dogbert"
	}
	
	/* The compiler automatically creates this function:
	new: static func ~withName (.name) -> This {
		this := This class alloc() as This
		init~withName(name)
		return this
	}
	*/
	
	init: func ~withName (=name)

}

main: func {

	dog := Dog new()
	printf("The dog's default name is %s\n", dog name)
	
	dog2 := Dog new("Scoobidoo")
	printf("A custom dog's name is %s\n", dog2 name)

}
