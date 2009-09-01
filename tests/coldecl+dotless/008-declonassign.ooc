add: func (left, right: Int) -> Int { left + right }

Dog: class {

	bark: func { println("Woof!") }
	
}

main: func {
	a := 1
	b := 2
	printf("1 + 2 = %d\n", add(a, b))
	
	dog := Dog new()
	dog bark()
}
