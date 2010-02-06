
/** Base class for all Interfaces */
Interface: class {
	realThis: Object
	funcs: Object
	
	init: func (=realThis, =funcs) {}
}

/** Killable interface - object class */
Killable: class extends Interface {
	kill: func { funcs as KillableFuncs doKill(realThis) }
	init: func (.realThis, funcs: KillableFuncs) { super(realThis, funcs as Object) }
}

/** Killable interface - funcs class */
KillableFuncs: class {
	doKill: Func (Object)

	init: func (=doKill) {}
}

Dog: class {
	japp: func { "Woof woof, says the dog" println() }
	kill: func { "Omg you just killed a dog!" println() }
}

/** Implement the Killable interface for Dog */
as_Killable: func ~dog (d: Dog) -> Killable {
	funcs := static null as KillableFuncs
	if(!funcs) funcs = KillableFuncs new(Dog kill)
	return Killable new(d, funcs)
}

Cat: class {
	kill: func { "Congrats, you killed a cat!" println() }
	meow: func { "Meooooww says the cat" println() }
}

as_Killable: func ~cat (c: Cat) -> Killable {
	funcs := static null as KillableFuncs
	if(!funcs) funcs = KillableFuncs new(Cat kill)
	return Killable new(c, funcs)
}

main: func {
	"Creating new Dog and killing it" println()
	d := Dog new()
	d kill()
	"Casting dog to killable and killing it" println()
	kd := as_Killable(d)
	kill(kd)
	
	"Creating new Cat and killing it" println()
	c := Cat new()
	c kill()
	"Casting cat to killable and killing it" println()
	kc := as_Killable(c)
	kill(kc)
}

kill: func (k: Killable) {
	printf("Class of k = %s, realClass of k = %s, class of realThis of k = %s\n", k class name, k funcs class name, k realThis class name)
	k kill()
}
