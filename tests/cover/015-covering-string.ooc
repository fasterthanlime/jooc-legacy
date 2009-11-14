import text/StringTokenizer

String: cover from Char* {

	split: func~withChar (c: Char) -> Iterable<String> {
		return StringTokenizer new(this, c)
	}

	split: func~withString (s: String) -> Iterable<String> {
		return StringTokenizer new(this, s)
	}

}

for (s:String in "ppc,i386,x86_64" split(",")) {
	s println()
}
