include stdlib, string

strlen: extern func (String) -> SizeT
strdup: extern func (String) -> String
atoi: extern func (String) -> Int
atol: extern func (String) -> Long

String: cover from Char* {
	
	length: func -> Int { strlen(this) }
	
	equals: func(other: String) -> Bool {
		if ((this == null) || (other == null)) {
			return false
		}
		if (this length() != other length()) {
			return false
		}
		for (i : Int in 0..other length()) {
			if (this[i] != other[i]) {
				return false
			}
		}
		return true
	}
	
	toInt: func -> Int { atoi(this) }
	
	toLong: func -> Long { atol(this) }
	
	toLLong: func -> LLong { atol(this) }
	
	/* TODO: toDouble */
	
	isEmpty: func -> Bool { (this == null) || (this[0] == 0) }
	
	startsWith: func(s: String) -> Bool {
		if (this length() < s length()) return false
		for (i: Int in 0..s length()) {
			if(this[i] != s[i]) return false
		}
		return true
	}
	
	endsWith: func(s: String) -> Bool {
		l1 = this length() : Int
		l2 = s length() : Int
		if(l1 < l2) return false
		offset = (l1 - l2) : Int
		for (i: Int in 0..l2) {
			if(this[i + offset] != s[i]) {
				return false
			}
		}
		return true
	}
	
	indexOf: func(c: Char) -> Int {
		for(i: Int in 0..this length()) {
			if(this[i] == c) {
				return i
			}
		}
		return -1
	}
	
	substring: func(start: Int) -> String {
		len = this length() : Int
		
		if(start > len) {
			printf("String.substring: out of bounds: length = %d, start = %d\n",
				len, start);
			return null
		}
		
		diff = (len - start) : Int
		sub := gc_malloc(diff + 1) as String
		sub[diff + 1] = 0
		memcpy(sub, this + start, diff)
		return sub
	}
	
	reverse: func -> String {
	
		len := this length()
	
		if (!len) {
			return null
		}
		
		result := gc_malloc(len + 1) as String
		for (i: Int in 0..len) {
			result[i] = this[(len-1)-i]
		}
		result[len] = 0
		
		return result
	}
	
	println: func {
		
		printf("%s\n", this)
		
	}
	
}
