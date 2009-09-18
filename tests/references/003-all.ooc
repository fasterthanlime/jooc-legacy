IntContainer: class {
	
	value: Int
	pointer : Int*
	
	new: func {
		pointer = gc_malloc(Int size)
	}
}

main: func {

	// regular pointer to int (allocate memory for the int)
	pointer := gc_malloc(Int size) as Int*
	pointer@ = 32
	printValue(pointer@)
	printPointer(pointer) // print expects a pointer: alright
	printReference(pointer)
	
	// now a simple int
	value: Int
	value = 64
	printValue(value)
	printPointer(value&) // we have to reference it
	printReference(value&)
	
	// Now use an IntContainer
	container := new IntContainer
	container pointer@ = 32
	printValue(container pointer@)
	printPointer(container pointer) // print expects a pointer: alright
	printReference(container pointer)
	
	container value = 64
	printValue(container value)
	printPointer(container value&) // we have to reference it
	printReference(container value&)
	

}

printValue: func (value: Int) {
	
	printf("Value of int is %d\n", value)
	
}

printPointer: func (pointer: Int*) {
	
	printf("Value of int pointer is %d\n", pointer@)
	
}

// Int@ acts like Int* except than when we write 'value' in the
// function body, it means 'value@' instead.
printReference: func (value: Int@) {
	
	printf("Value of int reference is %d\n", value)
	
}
