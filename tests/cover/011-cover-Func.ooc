A: cover from Func {
	
	println: func {
		printf("Func at %p!\n", this)
	}
	
}

main: func {
	
	main as A println()
	
}
