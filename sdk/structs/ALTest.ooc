import ArrayList

main: func {
	
	list := ArrayList<Int> new(Int);
	for(i in 0..10) {
		printf("%d, ", i)
		list add (i)
	}
	println()

}
