Named: class {
	name: String
	init: func (=name) {}
	sayName: func { printf("My dull name is %s\n", name) }
}

main: func {
	nm := Named new("Watson")
	nm sayName()
	"\n-- Saving old sayName implementation" println()
	oldSayName := Named sayName
	"\n-- Swapping sayName implementation.." println()
	Named sayName = evilSayName
	nm sayName()
	"\n-- Creating another Named to see what will happen.." println()
	Named new("Lou") sayName()
	"\n-- Restoring old implementation" println()
	Named sayName = oldSayName
	"\n-- Calling sayName on old instance" println()
	nm sayName()
}

evilSayName: func (this: Named) {
	printf("I'm the evil %s, mwahahahahahah\n", this name)
}
