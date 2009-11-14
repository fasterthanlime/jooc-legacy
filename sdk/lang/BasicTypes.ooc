include stdlib, stdio, ctype, stdint, stdbool

/**
 * Pointer type
 */
Void: cover from void
Pointer: cover from void*

/**
 * character and pointer types
 */
Char: cover from char
UChar: cover from unsigned char
WChar: cover from wchar_t

isalnum: extern func(letter: Int) -> Int
isalpha: extern func(letter: Int) -> Int
isdigit: extern func(letter: Int) -> Int
isspace: extern func(letter: Int) -> Int
tolower: extern func(letter: Int) -> Int
toupper: extern func(letter: Int) -> Int

Char: cover from char {

	isAlphaNumeric: func -> Bool {
		return isalnum(this)
	}
	
	isAlpha: func -> Bool { 
		return isalpha(this)
	}
	
	isDigit: func -> Bool {
		return isdigit(this)
	}
	
	isWhitespace: func() -> Bool {
		return isspace(this)
	}

	toLower: func() -> Char {
		return tolower(this)
	}

	toInt: func -> Int {
		if ((this >= 48) && (this <= 57)) {
			return (this - 48)
		}
		return -1
	}
	
	print: func { 
		printf("%c", this)
	}
	
	println: func {
		printf("%c\n", this)
	}
}

atoi: extern func (String) -> Int
atol: extern func (String) -> Long

String: cover from Char* {

    new: static func~withLength (length: SizeT) -> This {
		result : This = gc_malloc(length + 1)
		result[length] = 0
		result
	}
	
	new: static func~withChar (c: Char) -> This {
		result := This new~withLength(1)
		result[0] = c
		result
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
    
    contains: func (c: Char) -> Bool { indexOf(c) != -1 }
	
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
	
	last: func -> Char {
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
		sub[diff] = '\0'
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
		sub[diff] = 0
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
		copy as Char* [length] = other
		return copy
	}
    
    replace: func (oldie, kiddo: Char) -> This {
        length := length()
        for(i in 0..length) {
            if(this[i] == oldie) this[i] = kiddo
        }
        return this
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
    
    toLower: func -> This {
        copy := clone()
        length := length()
        for(i in 0..length) {
            copy[i] = tolower(copy[i])
        }
        return copy
    }
    
    toUpper: func -> This {
        copy := clone()
        length := length()
        for(i in 0..length) {
            copy[i] = toupper(copy[i])
        }
        return copy
    }

	charAt: func(index: SizeT) -> Char {
		this as Char* [index]
	}
}

operator == (str1: String, str2: String) -> Bool {
	return str1 equals(str2)
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

operator + (left: Bool, right: String) -> String {
	left toString() + right
}

operator + (left: String, right: Bool) -> String {
	left + right toString()
}

operator + (left: Double, right: String) -> String {
	left toString() + right
}

operator + (left: String, right: Double) -> String {
	left + right toString()
}

operator + (left: String, right: Char) -> String {
	left append(right)
}

operator + (left: Char, right: String) -> String {
	right prepend(left)
}

/**
 * integer types
 */
LLong: cover from long long {
	
	toString: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%lld", this)
		str
	}
	
	toHexString: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%llx", this)
		str
	}
	
	isOdd:  func -> Bool { this % 2 == 1 }
	isEven: func -> Bool { this % 2 == 0 }
	
	in: func(range: Range) -> Bool {
		return this >= range min && this < range max
	}
	
}

Int: cover from int {
	
	toString: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%d", this)
		str
	}
	
	isOdd:  func -> Bool { this % 2 == 1 }
	isEven: func -> Bool { this % 2 == 0 }
	
	in: func(range: Range) -> Bool {
		return this >= range min && this < range max
	}

	
}

UInt: cover from unsigned int extends LLong
Short: cover from short extends LLong
UShort: cover from unsigned short extends LLong
Long: cover from long extends LLong
ULong: cover from unsigned long extends LLong
ULLong: cover from unsigned long long extends LLong

/**
 * fixed-size integer types
 */
Int8: cover from int8_t
Int16: cover from int16_t
Int32: cover from int32_t
Int64: cover from int64_t

UInt8:  cover from uint8_t
UInt16: cover from uint16_t
UInt32: cover from uint32_t
UInt64: cover from uint64_t

Octet: cover from UInt8

SizeT: cover from size_t extends LLong

Bool: cover from bool {
	
	toString: func -> String { return this ? "true" : "false" }
	
}

/**
 * real types
 */
Float: cover from float extends Double {}
LDouble: cover from long double

Double: cover from double {
	
	toString: func -> String {
		str = gc_malloc(64) : String
		sprintf(str, "%.2f", this)
		str
	}
	
	abs: func -> This {
		return this < 0 ? -this : this
	}
	
}

/**
 * custom types
 */
Range: cover {

	min, max: Int
	
	new: static func (.min, .max) -> This {
		this : This
		this min = min
		this max = max
		return this
	}

}

/**
 * objects
 */
Class: abstract class {
	
	/// Number of octets to allocate for a new instance of this class 
	instanceSize: SizeT
	
	/// Number of octets to allocate to hold an instance of this class
	/// it's different because for classes, instanceSize may greatly
	/// vary, but size will always be equal to the size of a Pointer.
	/// for basic types (e.g. Int, Char, Pointer), size == instanceSize
	size: SizeT

	/// Human readable representation of the name of this class
	name: String
	
	/// Pointer to instance of super-class
	super: const Class
	
	/// Create a new instance of the object of type defined by this class
	alloc: final func -> Object {
		object := gc_malloc(instanceSize) as Object
		if(object) {
			object class = this
			object __defaults__()
		}
		return object
	}
	
	instanceof: final func (T: Class) -> Bool {
		if(this == T) return true
        return (super ? super as This instanceof(T) : false)
	}
	
	// workaround needed to avoid C circular dependency with _ObjectClass
	__defaults__: static Func (Class)
	__destroy__: static Func (Class)
	__load__: static Func
	
}

Object: abstract class {

	class: Class
    	
	/// Instance initializer: set default values for a new instance of this class
	__defaults__: func {}
	
	/// Finalizer: cleans up any objects belonging to this instance
	__destroy__: func {}
	
}

/**
 * iterators
 */
Iterator: abstract class <T> {

	hasNext: abstract func -> Bool
	next: abstract func -> T
    
    hasPrev: abstract func -> Bool
    prev: abstract func -> T
    
    remove: abstract func -> Bool
	
}

Iterable: abstract class <T> {

	iterator: abstract func -> Iterator<T>
	
}

Interface: class {
	realThis: Object
	funcs: Object
	
	init: func (=realThis, =funcs) {}
}


/**
 * exceptions
 */
Exception: class {

	origin: Class
	msg : String

	init: func (=origin, =msg) {}
	init: func ~noOrigin (=msg) {}
	
	crash: func {
		fflush(stdout)
		x := 0
		x = 1 / x
	}
	
	getMessage: func -> String {
		max := const 1024
		buffer := gc_malloc(max) as String
		if(origin) snprintf(buffer, max, "[%s in %s]: %s\n", class name, origin name, msg)
		else snprintf(buffer, max, "[%s]: %s\n", class name, msg)
		return buffer
	}
	
	print: func {
		fprintf(stderr, "%s", getMessage())
	}
	
	throw: func {
		print()
		crash()
	}

}

