include stdlib, string
import lang/[Int, LLong, Double, Range, stdio]

atoi: extern func (String) -> Int
atol: extern func (String) -> Long

String: cover from Char* {

	new: static func (length: SizeT) -> This {
		return gc_malloc(length)
	}
	
	length: extern(strlen) func -> SizeT
	
	equals: func(other: String) -> Bool {
		if ((this == null) || (other == null)) {
			return false
		}
		if (this length() != other length()) {
			return false
		}
		for (i : SizeT in 0..other length()) {
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
	toFloat: extern(atof) func -> Float
	
	isEmpty: func -> Bool { (this == null) || (this[0] == 0) }
	
	startsWith: func(s: String) -> Bool {
		if (this length() < s length()) return false
		for (i: SizeT in 0..s length()) {
			if(this[i] != s[i]) return false
		}
		return true
	}
	
	endsWith: func(s: String) -> Bool {
		l1 = this length() : SizeT
		l2 = s length() : SizeT
		if(l1 < l2) return false
		offset = (l1 - l2) : SizeT
		for (i: SizeT in 0..l2) {
			if(this[i + offset] != s[i]) {
				return false
			}
		}
		return true
	}
	
	indexOf: func(c: Char) -> SizeT {
		length := length()
		for(i: SizeT in 0..length) {
			if(this[i] == c) {
				return i
			}
		}
		return -1
	}
	
	trim: func ~space -> This { return trim(' ') }
	
	trim: func(c: Char) -> This {
		start := 0
		while(this[start] == c) start += 1;
		end := length()
		while(this[end - 1] == c) end -= 1;
		if(start != 0 || end != length()) return substring(start, end)
		return this
	}
	
	first: func -> SizeT {
		return this[0]
	}
	
	lastIndex: func -> SizeT {
		return length() - 1
	}
	
	last: func -> SizeT {
		return this[lastIndex()]
	}
	
	lastIndexOf: func(c: Char) -> SizeT {
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
	
	substring: func ~tillEnd (start: SizeT) -> This {
		len = this length() : SizeT
		
		if(start > len) {
			printf("String.substring~tillEnd: out of bounds: length = %zd, start = %zd\n",
				len, start);
			return null
		}
		
		diff = (len - start) : SizeT
		sub := gc_malloc(diff + 1) as This	
		sub[diff + 1] = 0
		memcpy(sub, this as Char* + start, diff)
		return sub
	}
	
	substring: func (start: SizeT, end: SizeT) -> This {
		len = this length() : SizeT
		
		if(start > len || start > end || end > len) {
			printf("String.substring: out of bounds: length = %zd, start = %zd, end = %zd\n",
				len, start, end);
			return null
		}
		
		diff = (end - start) : SizeT
		sub := gc_malloc(diff + 1) as This
		sub[diff + 1] = 0
		memcpy(sub, this as Char* + start, diff)
		return sub
	}
	
	reverse: func -> This {
	
		len := this length()
	
		if (!len) {
			return null
		}
		
		result := gc_malloc(len + 1) as This
		for (i: SizeT in 0..len) {
			result[i] = this[(len-1)-i]
		}
		result[len] = 0
		
		return result
	}
	
	print: func {
		printf("%s", this)
		fflush(stdout)
	}

	println: func {
		printf("%s\n", this)
	}
	
	times: func (count: Int) -> This {
		length := length()
		result := gc_malloc((length * count) + 1) as Char*
		for(i in 0..count) {
			memcpy(result + (i * length), this, length)
		}
		result[length * count] = '\0';
		return result
	}
	
	clone: func -> This {
		length := length()
		copy := gc_malloc(length + 1)
		memcpy(copy, this, length + 1)
		return copy
	}
	
	append: func(other: This) -> This {
		length := length()
		rlength := other length()
		copy := gc_malloc(length + rlength + 1) as Char*
		memcpy(copy, this, length)
		memcpy(copy as Char* + length, other, rlength + 1) // copy the final '\0'
		return copy
	}

	append: func ~char (other: Char) -> This {
		length := length()
		copy := gc_malloc(length + 2) as Char*
		memcpy(copy, this, length)
		copy as Char * [length - 1] = other
		return copy
	}
	
	prepend: func (other: String) -> This {
		other append(this)
	}

	prepend: func ~char (other: Char) -> This {
		length := length()
		copy := gc_malloc(length + 2) as Char*
		copy as Char * [0] = other
		memcpy(copy + 1, this, length)
		return copy
	}

	charAt: func(index: SizeT) -> Char {
		this as Char* [index]
	}
}

operator [] (string: String, index: SizeT) -> Char {
	string charAt(index)
}

operator * (str: String, count: Int) -> String {
	return str times(count)
}

operator + (left, right: String) -> String {
	return left append(right)
}

operator + (left: LLong, right: String) -> String {
	left toString() + right
}

operator + (left: String, right: LLong) -> String {
	left + right toString()
}

operator + (left: Int, right: String) -> String {
	left toString() + right
}

operator + (left: String, right: Int) -> String {
	left + right toString()
}

operator + (left: Double, right: String) -> String {
	left toString() + right
}

operator + (left: String, right: Double) -> String {
	left + right toString()
}

operator + (left: String, right: Char) -> String {
	return left append(right)
}

operator + (left: Char, right: String) -> String {
	return right prepend(left)
}
