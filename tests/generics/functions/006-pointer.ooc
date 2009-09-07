main: func {

	ptr := gc_malloc(128)
	val := 42
	mov(ptr, val)
	ints := ptr as Int*
	printf("Read int %d\n", ints[0])

}

mov: func <T> (ptr: Pointer, blah: T) {

	memcpy(ptr, blah&, T size)

}
