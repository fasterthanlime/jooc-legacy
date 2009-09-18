usleep: extern proto func (Int)

main: func {

	max := 10_000
	for(i in 0..max) {
		MemoryChunk new()
	}

}

MemoryChunk: class {

	data : Octet*

	init: func {
		data = gc_malloc(Octet size * 15000)
	}

	destroy: func {
		printf("Finalizer called!")
	}

}
