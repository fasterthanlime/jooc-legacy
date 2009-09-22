Exception: class {

	origin: Class
	msg : String

	init: func (=origin, =msg) {}
	init: func ~noOrigin (=msg) {}

	throw: func {
		if(origin) {
			printf("[%s in %s]: %s\n", class name, origin name, msg)
		} else {
			printf("[%s]: %s\n", class name, msg)
		
		crash()
	}
	
	crash: func {
		fflush(stdout)
		x := 0
		x = 1 / x
	}

}
