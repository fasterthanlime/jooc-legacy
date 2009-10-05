import structs/Array

Main: class {
	
    args: Array<String>
	
    init: func(=args) {
		run()
	}
	
	run: func {
		for(arg: String in args) {
			arg println()
		}
	}
	
}

main: func(args: Array<String>) {
	
	Main new(args)
	
}
