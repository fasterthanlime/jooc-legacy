import structs/LinkedList



Cacou: class {
	init: func {}
	
	draw: func {
	}
}

main: func {
	list := LinkedList<Cacou> new()
	for(i in 1..10) {
		list add(Cacou new())
	}
	
	for(n in list) {
        printf("n is a %s\n", n class name)
		//n draw()
	}
}