import structs/[List, ArrayList]

duplicate: func (list: ArrayList) -> ArrayList {
	
	T := list T
	dupe := ArrayList<T> new()
	
	//printf("Address of List iterator = %p\n", List iterator)
	//printf("Address of ArrayList iterator = %p\n", ArrayList iterator)
	//printf("Address of list iterator() = %p\n", list iterator)
	//printf("Address of list as List iterator() = %p\n", list as List iterator)
	//printf("Address of dupe iterator() = %p\n", dupe iterator)
	//printf("Address of dupe as List iterator() = %p\n", dupe as List iterator)
	
	//dupe addAll(list)
	return dupe
	
}

printType: func (object: Object) {
	
	T := object class
	printf("It's an %s", T name)
	if(T instanceof(List)) {
		printf("<%s>", object as ArrayList T name)
	}
	println()
	fflush(stdout)
	
}


main: func {

	chars := ArrayList<Char> new()
	chars add('a').add('b').add('c')
	printType(chars)
	chars2 := duplicate(chars)
	printType(chars2)
	
}
