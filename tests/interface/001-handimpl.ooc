Interface: class {
	realThis: Object
	
	init: func (=realThis) {}
}

Killable: class extends Interface {
	kill: inline func { doKill(realThis) }
	doKill: Func (Object)
	
	init: func ~child (.realThis, =doKill) { super(realThis) }
}

Dog: class {
	japp: func { "Woof woof, says the dog" println() }
	kill: func { "Omg you just killed a dog!" println() }
}

as_Killable: func ~dog (d: Dog) -> Killable { Killable new(d, d kill) }

Cat: class {
	kill: func { "Congrats, you killed a cat!" println() }
	meow: func { "Meooooww says the cat" println() }
}

as_Killable: func ~cat (c: Cat) -> Killable { Killable new(c, c kill) }

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
	k kill()
}
