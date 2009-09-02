Sayer: class {
	
	say: func {
		printf("Hi, I'm a Sayer, just sayin'...\n")
	}
	
}

main: func {
	
	Sayer new() say()
	
}
