import structs/HashMap

main: func {
	
	map := HashMap<String> new()
	for(i in 0..100) {
		key := "hobo" + i toString()
		map put(key, "haba")
		printf("Adding key %s\n", key)
	}
	
}
