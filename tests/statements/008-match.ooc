main: func(argc: Int, argv: String*) {
	
	if(argc <= 1) {
		printf("We need an argument here, dummy.\n")
		exit(0)
	}
	
	x := argv[1] toInt()
	msg : String = null
	
	match x {
		case 0  => "Loooooser!" println()
		case 1  => "So you're a lonely guy, huh?" println()
		case 2  => "C'mon there are rooms for that" println()
		case 3  => "Threesome = awesome" println()
		case 4  => msg = "Double-dating isn't playing."
		case 42 => "\\o/" println()
		case 	=> "Not very imaginative, are you?" println()
	}
	
	if(msg) {
        msg println()
        "...was a message brought to you by Match(TM)" println()
    }
	
}
