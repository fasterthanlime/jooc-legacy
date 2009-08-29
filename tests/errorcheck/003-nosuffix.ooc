main: func {
	call()
}

call: func {
	call(42)
}

call: func (value: Int) {
	printf("The answer is %d\n", value)
}
