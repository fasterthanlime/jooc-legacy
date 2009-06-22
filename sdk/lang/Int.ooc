cover Int {

	func binaryRepr -> String {

		String s = malloc(35); // 0b + 32*0,1 + \0
		memcpy(s, "0b000000000000000000000000000000000", 35);
		Int first = -1;
		for(Int i: reverse 0..32) {
			if(this & (1 << i)) {
				s[34 - i] = '1';
				if(first == -1) {
					first = 34 - i;
				}
			}
		}
		s[first - 1] = 'b';
		return s + first - 2;
	
	}

}
