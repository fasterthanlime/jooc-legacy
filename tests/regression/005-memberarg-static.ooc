Ant: class {
	
	initialLife = 20 : static Int
	
	setInitialLife: static func (=initialLife) {}
	
}

main: func {
	
	printf("Ant initialLife = %d\n", Ant initialLife)
	Ant setInitialLife(40)
	printf("Now, ant initialLife = %d\n", Ant initialLife)
	
}
