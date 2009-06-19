include stdio;
include math;

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
	
	//TODO eagle2com: Add negative indexes!!!
	func subrange(Int index, Int range) -> String {
		
		String substr = malloc(abs(range) + 1);
		Int min;
		Int max;
		
		if(range > 0){
			min = index;
			max = index + range ;
		}
		else if(range < 0){
			min = index + range;
			max = index;
		}
		else if(index >= 0 && index < this.length){
			String s = malloc(2);
			s[0] = this[index];
			s[1] = '\0';
			
			return s;
		}
		else{
			printf("String: out of bound exception\n");
			Int x = 0;
			x = x/x;
		}
		
		for(Int i:min..max){
			if(i >= 0 && i < this.length){
				substr[i-min] = this[i];
			}
		}
		substr[max+1]='\0';
		return substr;
	}
	
}
