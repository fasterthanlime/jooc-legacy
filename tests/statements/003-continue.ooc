main: func {

	willContinue := true
	willBreak := false
	i := 0

	while (true) {
		printf ("Run %d\n", i += 1)
		if (willContinue) {
			"Continuing.." println()
			willContinue = false
			continue
		} else if(willBreak) {
			"Breaking!" println()
			break;
		} else  {
			"Now will break" println()
			willBreak = true
		}
		"Loop ended normally" println()
	}

}
