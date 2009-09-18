func1: func <T> (arg: T) {
	printf("Got a %s also\n", T name)
}

func2: func <T> (arg: T) {
	printf("Got a %s\n", T name)
    func1(arg)
}

func2("Hi world.")
