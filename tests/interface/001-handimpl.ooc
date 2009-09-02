Interface: class {
	realThis: Object
	funcs: Object
	
	init: func (=realThis, =funcs) {}
}

Killable: class extends Interface {
	kill: inline func { funcs as KillableFuncs doKill(realThis) }
	init: func ~child (.realThis, funcs: KillableFuncs) { super(realThis, funcs) }
}

KillableFuncs: class {
	doKill: Func (Object)

	init: func (=doKill) {}
}

Dog: class {
	japp: func { "Woof woof, says the dog" println() }
	kill: func { "Omg you just killed a dog!" println() }
}

as_Killable: func ~dog (d: Dog) -> Killable {
	funcs := static null as KillableFuncs
	if(!funcs) {
		"First time casting a Dog to Killable, initialiazing funcs"
		funcs = KillableFuncs new(Dog kill)
	}
	return Killable new(d, funcs)
}

Cat: class {
	kill: func { "Congrats, you killed a cat!" println() }
	meow: func { "Meooooww says the cat" println() }
}

//as_Killable: func ~cat (c: Cat) -> Killable { Killable new(c, c kill) }

main: func {
	"Creating new Dog and killing it" println()
	d := Dog new()
	d kill()
	"Casting dog to killable and killing it" println()
	kd := as_Killable(d)
	kill(kd)

	/*
	"Creating new Cat and killing it" println()
	c := Cat new()
	c kill()
	"Casting cat to killable and killing it" println()
	kc := as_Killable(c)
	kill(kc)
	*/
}

kill: func (k: Killable) {
	printf("Class of k = %s, realClass of k = %s, class of realThis of k = %s\n", k class name, k funcs class name, k realThis class name)
	k kill()
}
