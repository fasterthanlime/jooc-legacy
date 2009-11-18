main: func {

	// demonstrate compiler error messages, e.g. a Float won't
	// fit in an Int array. Then again, maybe it should guess they
	// are all Floats?
	array := [3.14, 2, 3, 4]
	for(i in 0..4) {
		printf("array[%d] = %f\n", i, array[i])
	}

}
