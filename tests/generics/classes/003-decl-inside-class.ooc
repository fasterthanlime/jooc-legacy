Provider: class <T> {
	
	init: func (=T) {}

	provide: func -> T {
		a := 42
		return a
	}
	
}

Getter: class <T> {
	
	init: func (=T) {}
	
	get: func(prov: Provider) -> T {
		element: T
		element = prov provide()
		return element
	}
	
}

main: func {

	prov := Provider<Int> new(Int)
	printf("The answer is %d\n", prov provide())
	gett := Getter<Int> new(Int)
	printf("The answer is also %d\n", gett get(prov))

}
