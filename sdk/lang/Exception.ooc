Exception: class {

	origin: Class
	msg : String

	init: func (=origin, =msg) {}
	init: func ~noOrigin (=msg) {}

	throw: func {
		printf("[%s in %s]: %s\n", class name, origin name, msg)
		fflush(stdout)
		x := 0
		x = 1 / x
	}

}
