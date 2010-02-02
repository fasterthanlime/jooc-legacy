import text/regexp/Regexp

main: func {
	r := Regexp new("^test$")
	if (r matches("test")) {
		"match!" println()
	}
}