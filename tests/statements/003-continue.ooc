main: func {

	willContinue := true
	willBreak := true
	i := 0

	while (true) {
		printf ("Run %d\n", i++)
		if (willContinue) {
			printf ("Continue-ing\n")
			willContinue = false;
			continue;
		}
		if (!willBreak) {
			printf ("Breaking\n")
			willBreak = false;
		} else {
			break;
		}
		printf ("Loop ended normally\n")
	}

}
