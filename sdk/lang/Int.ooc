cover Int {

	func binaryRepr -> String {
	
		String s = "0b00000000000000000000000000000000";
		for(Int i: 0..32) {
			if((this << i) & 1) {
				s[i + 2] = '1';
			}
		}
		return s;
	
	}

}
