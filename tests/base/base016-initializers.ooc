Foo: class {
	
	value = 99 : Int
	msg: String
	
	new: func (=msg) { // in ooc, you can omit the type if it's a member variable's name, and '=' means to automatically assign it.
		printf("msg = %s, value = %d\n", msg, this value)
	}
	
}

main: func {
	
	new Foo("Dilbert")
	
}
