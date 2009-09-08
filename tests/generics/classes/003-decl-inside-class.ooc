Provider: class <T> {
	
	init: func (=T) {}

	provide: func -> T {
		a := 42
		return a
	}
	
}

Getter: class <T> {

	field : T
	data : T*
	
	init: func (=T) {
		field = gc_malloc(T size)
		data = gc_malloc(T size)
	}
	
	get: func(prov: Provider) -> T {
		element: T
		element = prov provide()
		return element
	}
	
	get2: func(prov: Provider) -> T {
		field = prov provide()
		return field
	}
	
	get3: func(prov: Provider) -> T {
		data@ = prov provide()
		return data@
	}
	
}

main: func {

	prov := Provider<Int> new(Int)
	printf("The answer is %d\n", prov provide())
	gett := Getter<Int> new(Int)
	printf("The answer is also %d\n", gett get(prov))
	printf("The answer is %d, too.\n", gett get2(prov))
	printf("The answer is still %d\n", gett get3(prov))

}
