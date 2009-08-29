main: func {
	call()
}

call: func {
	call(42)
	call~withValue(42)
}

call: func ~withValue (value: Int) {
	printf("The answer is %d\n", value)
}
