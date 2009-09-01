Named: abstract class {
	name: String
	init: func (=name) {}
	sayName: abstract func
	morph: func { class = PrettyNamed }
}

DullNamed: class extends Named {
	init: func ~name (.name) { super(name) }
	sayName: func { printf("My dull name is %s\n", name) }
}

PrettyNamed: class extends Named {
	init: func ~name (.name) { super(name) }
	sayName: func { printf("{{ My Pretty name is: ~The %s~ }}\n", name) }
}

main: func {
	nm := DullNamed new("Watson") as Named
	printf("nm is a %s\n", nm class name)
	nm sayName()
	nm morph()
	printf("nm is now a %s\n", nm class name)
	nm sayName()
}
