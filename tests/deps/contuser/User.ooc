import Container

User: class {

	cont := Container new()

	init: func (=cont) {
		("Omfg, it compiles, and cont is of type " + cont class name) println()
	}

}
