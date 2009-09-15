
gcd: func (u, v : Int) -> Int {

	printf("Computing gcd of (%d, %d)\n", u, v)
	
	if(u == 0) return v
	if(v == 0) return u

	if(u isEven()) {
		if(v isEven()) {
			return 2 * gcd(u / 2, v / 2)
		} else {
			return gcd(u / 2, v)
		}
	} else {
		if(v isOdd()) {
			if(u > v) {
				return gcd((u - v) / 2, v)
			} else {
				return gcd((v - u) / 2, u)
			}
		}
	}
	
	return gcd(v, u)

}

main: func(argc: Int, argv: String*) {

	a, b : Int

	if(argc >= 3) {
		a = argv[1] as String toInt()
		b = argv[2] as String toInt()
	} else {
		a = 325
		b = 707
	}

	printf("gcd of (%d, %d) = %d\n", a, b, gcd(a, b))

}
