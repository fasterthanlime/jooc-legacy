Foo: class extends Exception {

	// this is okay
	//init: func (a: Class, b: String) {}
	
	// this is okay too
	//init: func ~dumbSuffix {}
	
	// this is not okay
	init: func {}
	
}
