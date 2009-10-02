MyArray: class <T> {

	data : T*
	data2: T*
	data3: Octet*
	data4: Octet*
	
	blah: func {
		data = gc_malloc(42)
		this data2 = gc_malloc(42)
		data3 = gc_malloc(42)
		this data4 = gc_malloc(42)
		"Allocated alright!" println()
	}

}
