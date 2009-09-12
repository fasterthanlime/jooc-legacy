Container: class <T> {
	get: func -> T {
		return 42
	}
}

makeContainer: func (V: Class) -> Container {
	return Container<V> new()
}

main: func {
	cont := makeContainer(Int)
	cont get()
}
