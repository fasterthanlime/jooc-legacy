import structs.[List, ArrayList]

main: func {
	
	max := 10
	
	list := ArrayList<Int> new(Int);
	
	for(i in 0..max) {
		printf("Adding %d\n", i)
		list add (i)
	}
	println()
	
	for(i in 0..max) {
		printf("list[%d] = %d\n", i, list get(i))
	}
	println()

	for(i in 0..max) {
		printf("list[%d] = %d\n", i, list[i])
	}

}
