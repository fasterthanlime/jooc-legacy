import text/StringTokenizer

main: func() {
	st := StringTokenizer new("This is a test", " ")
	
	for (s: String in st)
		s println()
}