Blah: class {

	init: func {
		printf("Regular init()\n")
	}

	init: func ~msg (msg: String) {
		printf("init(msg) = %s\n", msg)
	}

}

Blah new()
Blah new("Hi world")
