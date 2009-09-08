main: func {

	x = 1.0, y = 4.2, z = 3.7 : Float
	printf("Assigned to values (%f, %f, %f)\n", x, y, z);
	for(f: Float in [x, y, z]) {
		printf("Printing value %f\n", f);
	}

}
