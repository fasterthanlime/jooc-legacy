cover Int {

	func binaryRepr -> String {

		String s = malloc(35); // 0b + 32*0,1 + \0
		memcpy(s, "0b00000000000000000000000000000000", 35);
		Int last = 0;
		for(Int i: 0..31) {
			if(this & (1 << i)) {
				s[i + 2] = '1';
				//if(first == -1) {
				//	first = i;
				//}
			}
		}
		//s[last + 3] = '\0';
		return s;
	
	}

}
