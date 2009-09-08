include stdlib, string

atoi: extern func (String) -> Int
atol: extern func (String) -> Long

String: cover from Char* {
	
	length: extern(strlen) func -> SizeT
	
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
	
	toInt: extern(atoi) func -> Int
	toLong: extern(atol) func -> Long
	toLLong: extern(atoll) func -> LLong
	toDouble: extern(atof) func -> Double
	
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
		length := length()
		for(i: Int in 0..length) {
			if(this[i] == c) {
				return i
			}
		}
		return -1
	}
	
	trim: func(c: Char) -> This {
		start := 0
		while(this[start] == c) start += 1;
		end := length()
		while(this[end - 1] == c) end -= 1;
		if(start != 0 || end != length()) return substring(start, end)
		return this
	}
	
	lastIndexOf: func(c: Char) -> Int {
		// could probably use reverse foreach here
		i := length()
		while(i) {
			if(this[i] == c) {
				return i
			}
			i -= 1
		}
		return -1
	}
	
	substring: func ~tillEnd (start: Int) -> This {
		len = this length() : Int
		
		if(start > len) {
			printf("String.substring~tillEnd: out of bounds: length = %d, start = %d\n",
				len, start);
			return null
		}
		
		diff = (len - start) : Int
		sub := gc_malloc(diff + 1) as This
		sub[diff + 1] = 0
		memcpy(sub, this + start, diff)
		return sub
	}
	
	substring: func (start: Int, end: Int) -> This {
		len = this length() : Int
		
		if(start > len || start > end || end > len) {
			printf("String.substring: out of bounds: length = %d, start = %d, end = %d\n",
				len, start, end);
			return null
		}
		
		diff = (end - start) : Int
		sub := gc_malloc(diff + 1) as This
		sub[diff + 1] = 0
		memcpy(sub, this + start, diff)
		return sub
	}
	
	reverse: func -> This {
	
		len := this length()
	
		if (!len) {
			return null
		}
		
		result := gc_malloc(len + 1) as This
		for (i: Int in 0..len) {
			result[i] = this[(len-1)-i]
		}
		result[len] = 0
		
		return result
	}
	
	println: func {
		printf("%s\n", this)
	}
	
	times: func (count: Int) -> This {
		length := length()
		result := gc_malloc((length * count) + 1) as String
		for(i in 0..count) {
			memcpy(result + (i * length), this, length)
		}
		result[length * count] = '\0';
		return result
	}
	
}

operator * (str: String, count: Int) -> String {
	return str times(count)
}
