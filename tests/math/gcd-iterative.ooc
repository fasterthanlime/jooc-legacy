
gcd: func (u, v : Int) -> Int {

	factor := 0
	result := -1
	
	while(true) {
		
		printf("Computing gcd of (%d, %d)\n", u, v)
	
		if(u == 0) { result = v; break }
		if(v == 0) { result = u; break }

		if(u isEven()) {
			if(v isEven()) {
				factor += 2
				u /= 2
				v /= 2
				continue
			} else {
				u /= 2
				continue
			}
		} else {
			if(v isOdd()) {
				if(u > v) {
					u = (u - v) / 2
					continue
				} else {
					tmp := u
					u = (v - u) / 2
					v = tmp
					continue
				}
			}
		}
	
		tmp := u
		u = v
		v = tmp
			
	}
	
	return result * factor

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
