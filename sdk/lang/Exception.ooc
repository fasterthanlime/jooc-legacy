Exception: class {

	origin: Class
	msg : String

	init: func (=origin, =msg) {}

	throw: func {
		printf("[%s in %s]: %s\n", this class name, origin name, msg)
		fflush(stdout)
		x := 0
		x = 1 / x
	}

}
