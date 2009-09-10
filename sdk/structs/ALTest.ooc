import structs.[List, ArrayList]

main: func {
	
	max := 4
	
	list := ArrayList<Int> new(Int);
	
	for (i in 0..(max / 2)) list add (i)
	//for (i in max / 2..max) list += i
	
	for (i in 0..max) printf("list[%d] = %d\n", i, list get(i))
	println()

	for (i in 0..max) printf("list[%d] = %d\n", i, list[i])
	
	"\nadjusting an element.." println()
	list[3] = 42
	for (i in 0..max) printf("list[%d] = %d\n", i, list[i])
	
	//"\nremoving an element.." println()
	//list -= 42
	//for (i in 0..list size()) printf("list[%d] = %d\n", i, list[i])

}
