main: func(argc: Int, argv: String*) {
	
	if(argc <= 1) {
		printf("We need an argument here, dummy.\n")
		exit(0)
	}
	
	x := argv[1] toInt()
	
	match {
		case x in(0..3) => "Man, you suck at school"		
		case x in(2..5) => "Hehe, too bad. Just too bad."
		case x in(4..7) => "So much win!"
		case x > 6		=> "Have you been cheating or what?"
		case			=> "Huh what's " + x + " for a grade?"
	} println()
	
}
