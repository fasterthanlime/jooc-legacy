Negate: class {	

}

operator [] (negate: Negate, index: Int) -> Int {
	0 - index;
}

main: func {
	negate := Negate new()
	for (i: Int in 0..10) {
		printf("Negate of %d is %d\n", i, negate[i])
	}
}
