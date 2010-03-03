import text/regexp/Regexp

main: func {
	r := Regexp new("t(.*?)t(.*?) another")
    
    data := "This is a test, and that another test."
	if (r matches(data)) {
		"match!" println()
	}
    
    matches := r getMatches(data)
    if(matches) {
        for (m in matches) {
            printf("Found match '%s'\n", m)
        }
    } else {
        "No matches!" println()
    }
}