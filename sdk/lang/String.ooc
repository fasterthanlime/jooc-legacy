include stdio;

cover String {
	
	func length -> Int {
		return strlen(this);
	}
	
	func equals(String s) -> Bool {

		Int l1 = this.length;
		Int l2 = s.length;
		if(l1 != l2) {
			return false;
		}
		
		for(Int i: 0..l2) {
			if(this[i] != s[i]) {
				return false;
			}
		}
		
		return true;
		
	}
	
	func startsWith(String s) -> Bool {

		Int l1 = this.length;
		Int l2 = s.length;
		if(l1 < l2) {
			return false;
		}
		
		for(Int i: 0..l2) {
			if(this[i] != s[i]) {
				return false;
			}
		}
		
		return true;
		
	}
	
	func endsWith(String s) -> Bool {

		Int l1 = this.length;
		Int l2 = s.length;
		if(l1 < l2) {
			return false;
		}
		Int off = l1 - l2;
		
		for(Int i: 0..l1) {
			if(this[i + off] != s[i]) {
				return false;
			}
		}
		
		return true;
		
	}
	
	func indexOf(Char c) -> Int {
		
		for(Int i: 0..length) {
			if(this[i] == c) {
				return i;
			}
		}
		
		return -1;
		
	}
	
	func lastIndexOf(Char c) -> Int {
		
		for(Int i = length - 1; i >= 0; i--) {
			if(this[i] == c) {
				return i;
			}
		}
		
		return -1;
		
	}
	
}
