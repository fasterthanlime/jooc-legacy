main: func(argc: Int, argv: String*) {
	
	if(argc <= 1) {
		printf("We need an argument here, dummy.\n")
		exit(0)
	}
	
	x := argv[1] toInt()
	
	msg := match {
		case 0 <= x && x <= 2
			=> "Man, you suck at school"
			
		case 2 < x && x <= 4
			=> "Hehe, too bad. Just too bad."
			
		case 4 < x && x <= 6
			=> "So much win!"
			
		case 6 < x
			=> "Have you been cheating or what?"
			
		case
			=> "Huh what's " + x + " for a grade?"
	}
	msg println()
	
}
