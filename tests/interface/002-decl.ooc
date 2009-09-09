
Representable: interface {
	repr: func -> String
}

Person: class {
	name : String
	age : Int32

	init: new(=name, =age)
}

implement Representable for Person

print: func ~repr (object: Representable) {
	object repr() println()
}

main: func {
	p := Person new("Johnny", 24)
	print(p as Representable)
}
