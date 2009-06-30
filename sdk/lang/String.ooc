include stdio;
include math;

import Char;

cover String {

	/**
	 * @return the length of this string.
	 */
	func length -> Int {
		return strlen(this);
	}
	
	/**
	 * Compares this string to the specified object. The result is true
	 * if and only if the argument is not null and is a String object
	 * that represents the same sequence of characters as this object. 
	 * @return true if this string is equal to the argument, false
	 * otherwise
	 */
	func equals(String s) -> Bool {

		if(this == null || s == null) {
			return false;
		}

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

	/**
	 * @return this String parsed as an Int
	 */
	func toInt -> Int {
		return atoi(this);
	}
	
	/**
	 * @return this String parsed as a Long
	 */
	func toLong -> Long {
		return atol(this);
	}
	
	/**
	 * @return this String parsed as a LLong (long long)
	 */
	func toLLong -> LLong {
		return atol(this);
	}
	
	/**
	 * @return this String parsed as a Double
	 */
	func toDouble -> Double {
		
		// Amos Wenger: Standard C atof() depends on locale: imho, it sucks
		// in most cases, it's a comma that's needed.
		Int intPart = 0;
		Double floatPart = 0.0;
		Double factor = 0.1;
		
		Bool afterDot = false;
		
		Int minus = 1;
		Int start = 0;
		if(this[0] == '-') {
			minus = -1;
			start = 1;
		}
		
		for(Int i: start..this.length) {
			Char c = this[i];
			if(afterDot) {
				Int add = c.toInt;
				if(add == -1) {
					break; // Ignore invalid input
				}
				floatPart += factor * add;
				factor *= 0.1;
			} else {
				if(c == '.') {
					afterDot = true;
				} else {
					Int add = c.toInt;
					if(add == -1) {
						break; // Ignore invalid input
					}
					intPart *= 10;
					intPart += add;
				}
			}
		}
		
		return minus * (intPart + floatPart);
		
	}
	
	/**
	 * @return true if this String is empty
	 */
	func isEmpty -> Bool {
		return this == null || this[0] == '\0';
	}
	
	/**
	 * Tests if this string starts with the specified prefix. 
	 */
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
	
	/**
	 * Tests if this string ends with the specified prefix. 
	 */
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
	
	/**
	 * @return the index within this string of the first occurrence of
	 * the specified character.
	 */
	func indexOf(Char c) -> Int {
		
		for(Int i: 0..length) {
			if(this[i] == c) {
				return i;
			}
		}
		
		return -1;
		
	}
	
	/**
	 * @return the index within this string of the last occurrence of
	 * the specified character.
	 */
	func lastIndexOf(Char c) -> Int {
		
		for(Int i = length - 1; i >= 0; i--) {
			if(this[i] == c) {
				return i;
			}
		}
		
		return -1;
		
	}
	
	/**
	 * @return a copy of a subset of this string, beginning
	 * at the specified index.
	 */
	func substring(Int start) {
		
		Int _length = length();
		if(start > _length) {
			printf("String.substring: out of bounds exception. length = %d, start = %d\n",
				_length, start);
			Int x = 0;
			x = x/x;
		}
		
		Int diff = _length - start;
		String sub = malloc(diff + 1);
		sub[diff + 1] = '\0';
		memcpy(sub, this + start, diff);
		return sub;
		
	}
	
	//TODO eagle2com: Add negative indices.
	func subrange(Int index, Int range) -> String {
		
		Int absRange = abs(range);
		String substr = malloc(absRange + 1);
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
			printf("String.subrange: out of bounds exception. length = %d, index = %d, range = %d\n",
				this.length, index, range);
			Int x = 0;
			x = x/x;
		}
		
		for(Int i:min..max){
			if(i >= 0 && i < this.length){
				substr[i-min] = this[i];
			}
		}
		substr[absRange]='\0';
		return substr;
	}
	
	/**
	 * @return a reversed copy of this String
	 */
	func reverse -> String {
	
		Int _length = this.length;
	
		if(!_length) { // No empty strings, please! 
			return null;
		}
		
		String result = malloc(_length + 1);
		result[_length] = '\0';
		String p = this + _length;
		String q = result;
		
		while(*(--p)) {
			*(q++) = *p;
		}
		
		return result;
		
	}
	
}
