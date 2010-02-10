import text/StringBuffer

/**
    Note: StringBuffer is depreciated. Use Buffer instead.
*/
main: func {

	sb := StringBuffer new()
	for(i in 0..10) {
		sb append("yay =D ")
	}
	sb toString() println()

}
