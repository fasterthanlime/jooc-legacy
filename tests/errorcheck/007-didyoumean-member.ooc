main: func {
	// intentional typo, to test the error checking.
	printf("Speaker's value = %d\n", Speaker new() valu);
}

Speaker: class {
	value: Int
}
