main: func(argc: Int, argv: String*) {
	
	if(argc <= 1) {
		printf("We need an argument here, dummy.\n")
		exit(0)
	}
	
	x := argv[1] toInt()
	match true {
		case 0 <= x && x <= 2
            => "Man, you suck at school" println()
		case 2 < x && x <= 4
			=> "Hehe, too bad. Just too bad." println()
		case 4 < x && x <= 6
			=> "So much win!" println()
		case (6 < x)
			=> "Have you been cheating or what?" println()
		case
			=> ("Huh what's " + x + " for a grade?") println()
	}
	
}
