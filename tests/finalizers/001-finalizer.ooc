Octet: extern Octet
usleep: extern func (Int)

main: func {

	max := 10_000
	for(i: Int in 0..max) {
		new MemoryChunk
	}

}

MemoryChunk: class {

	data : Octet*

	new: func {
		data = GC_malloc(sizeof(Octet) * 15000)
	}

	destroy: func {
		printf("Finalizer called!")
	}

}
