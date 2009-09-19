import structs/HashMap

printHashMap: func (hm: HashMap<String>) {
    a := hm keys /* without this line, I get `Iterating over.. not a Range but a ArrayList` */
    for(s: String in hm keys) {
        s println()
    }
}

main: func {
	
	hm := HashMap<String> new()
	hm put("too", "2")
	hm put("much", "8")
	hm put("pressure", "7")
	printHashMap(hm)
	
}
