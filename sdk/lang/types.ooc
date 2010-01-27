import structs/ArrayList /* for Iterable<T> toArrayList */
import text/StringBuffer /* for String replace ~string */

include stddef, stdlib, stdio, ctype, stdint, stdbool

/**
 * Pointer type
 */
Void: cover from void
Pointer: cover from void*

/**
 * character and pointer types
 */
Char: cover from char {
    // check for an alphanumeric character
    isAlphaNumeric: func -> Bool {
        isAlpha() || isDigit()
    }

    // check for an alphabetic character
    isAlpha: func -> Bool {
        isLower() || isUpper()
    }

    // check for a lowercase alphabetic character
    isLower: func -> Bool {
        this >= 'a' && this <= 'z'
    }

    // check for an uppercase alphabetic character
    isUpper: func -> Bool {
        this >= 'A' && this <= 'Z'
    }

    // check for a decimal digit (0 through 9)
    isDigit: func -> Bool {
        this >= '0' && this <= '9'
    }

    // check for a hexadecimal digit (0 1 2 3 4 5 6 7 8 9 a b c d e f A B C D E F)
    isHexDigit: func -> Bool {
        isDigit() ||
        (this >= 'A' && this <= 'F') ||
        (this >= 'a' && this <= 'f')
    }

    // check for a control character
    isControl: func -> Bool {
        (this >= 0 && this <= 31) || this == 127
    }

    // check for any printable character except space
    isGraph: func -> Bool {
        isPrintable() && this != ' '
    }

    // check for any printable character including space
    isPrintable: func -> Bool {
        this >= 32 && this <= 126
    }

    // check for any printable character which is not a space or an alphanumeric character
    isPunctuation: func -> Bool {
        isPrintable() && !isAlphaNumeric() && this != ' '
    }

    // check for white-space characters: space, form-feed ('\f'), newline ('\n'),
    // carriage return ('\r'), horizontal tab ('\t'), and vertical tab ('\v')
    isWhitespace: func -> Bool {
        this == ' '  ||
        this == '\f' ||
        this == '\n' ||
        this == '\r' ||
        this == '\t' ||
        this == '\v'
    }

    // check for a blank character; that is, a space or a tab
    isBlank: func -> Bool {
        this == ' ' || this == '\t'
    }

    toInt: func -> Int {
        if (isDigit()) {
            return (this - '0')
        }
        return -1
    }

    toLower: extern(tolower) func -> This

    toUpper: extern(toupper) func -> This

    toString: func -> String {
        String new(this)
    }
    
    print: func { 
        printf("%c", this)
    }
    
    println: func {
        printf("%c\n", this)
    }
}

SChar: cover from signed char extends Char
UChar: cover from unsigned char extends Char
WChar: cover from wchar_t

operator as (value: Char) -> String {
    value toString()
}

atoi: extern func (String) -> Int
atol: extern func (String) -> Long

String: cover from Char* {

    new: static func~withLength (length: SizeT) -> This {
        result : This = gc_malloc(length + 1)
        result[length] = '\0'
        result
    }
    
    new: static func~withChar (c: Char) -> This {
        result := This new~withLength(1)
        result[0] = c
        result
    }

    /** compare `length` characters of `this` with `other`, starting at `start`. */
    compare: func (other: This, start, length: SizeT) -> Bool {
        for(i: SizeT in 0..length) {
            if(this[start + i] != other[i]) {
                return false
            }
        }
        return true
    }

    compare: func ~implicitLength (other: This, start: SizeT) -> Bool {
        compare(other, start, other length())
    }

    compare: func ~whole (other: This) -> Bool {
        compare(other, 0, other length())
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

    startsWith: func~withChar(c: Char) -> Bool {
        return this[0] == c
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
    
    indexOf: func ~charZero (c: Char) -> Int {
        indexOf(c, 0)
    }
    
    indexOf: func ~char (c: Char, start: Int) -> Int {
        length := length()
        for(i: Int in 0..length) {
            if(this[i] == c) {
                return i
            }
        }
        return -1
    }
    
    indexOf: func ~stringZero (s: This) -> Int {
        indexOf(s, 0)
    }
    
    indexOf: func ~string (s: This, start: Int) -> Int {
        length := length()
        slength := s length()
        for(i: Int in start..length) {
            if(compare(s, i, slength))
                return i
        }
        return -1
    }
    
    contains: func ~char (c: Char) -> Bool { indexOf(c) != -1 }
    
    contains: func ~string (s: This) -> Bool { indexOf(s) != -1 }

    trim: func ~space -> This { return trim(' ') }
    
    trim: func(c: Char) -> This {
        if(length() == 0) return this
        
        start := 0
        while(this[start] == c) start += 1;
        
        end := length()
        if(start >= end) return ""
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
            Exception new(This, "substring") throw()
            return null
        }
        
        diff = (len - start) : SizeT
        sub := gc_malloc(diff + 1) as This    
        memcpy(sub, this as Char* + start, diff)
        sub[diff] = '\0'
        return sub
    }
    
    substring: func (start: SizeT, end: SizeT) -> This {
        len = this length() : SizeT
        
        if(start == end) return ""
        
        if(start > len || start > end || end > len) {
            printf("String.substring: out of bounds: length = %zd, start = %zd, end = %zd\n",
                len, start, end);
            Exception new(This, "substring") throw()
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
        copy as Char* [length + 1] = '\0'
        return copy
    }

    count: func ~char (what: Char) -> SizeT {
        count := 0
        for(i: SizeT in 0..length()) {
            if(this[i] == what)
                count += 1
        }
        return count
    }

    count: func ~string (what: String) -> SizeT {
        length := this length()
        whatLength := what length()
        count := 0
        i := 0
        while(i < length) {
            if(compare(what, i, whatLength)) {
                count += 1
                i += whatLength 
            } else {
                i += 1
            }
        }
        return count
    }
    
    replace: func (oldie, kiddo: Char) -> This {
        length := length()
        for(i in 0..length) {
            if(this[i] == oldie) this[i] = kiddo
        }
        return this
    }

    replace: func ~string (oldie, kiddo: This) -> This {
        length := length()
        oldieLength := oldie length()
        buffer := StringBuffer new(length)
        i: SizeT = 0
        while(i < length) {
            if(compare(oldie, i, oldieLength)) {
                /* found oldie! */
                buffer append(kiddo)
                i += oldieLength
            } else {
                buffer append(this as Char* [i])
                i += 1    
            }
        }
        buffer toString()
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
            copy[i] = copy[i] toLower()
        }
        return copy
    }
    
    toUpper: func -> This {
        copy := clone()
        length := length()
        for(i in 0..length) {
            copy[i] = copy[i] toUpper()
        }
        return copy
    }

    charAt: func(index: SizeT) -> Char {
        this as Char* [index]
    }

    format: func (...) -> String {
        list:VaList

        va_start(list, this)
        length := vsnprintf(null, 0, this, list) + 1
        output: String = gc_malloc(length)
        va_end(list)

        va_start(list, this)
        vsnprintf(output, length, this, list)
        va_end(list)

        return output
    }

    scanf: func (format: This, ...) -> Int {
        list: VaList

        va_start(list, format)
        retval := vsscanf(this, format, list)
        va_end(list)
        
        return retval
    }
    
    iterator: func -> StringIterator<Char> {
        StringIterator<Char> new(this)
    }
    
}

operator == (str1: String, str2: String) -> Bool {
    return str1 equals(str2)
}

operator != (str1: String, str2: String) -> Bool {
    return !str1 equals(str2)
}

operator [] (string: String, index: SizeT) -> Char {
    string charAt(index)
}

operator [] (string: String, range: Range) -> String {
    string substring(range min, range max)
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

operator as (value: LLong) -> String {
    value toString()
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

operator as (value: Int) -> String {
    value toString()
}

UInt: cover from unsigned int extends LLong
Short: cover from short extends LLong
UShort: cover from unsigned short extends LLong
Long: cover from long extends LLong
ULong: cover from unsigned long extends LLong

ULLong: cover from unsigned long long {
    
    toString: func -> String {
        str = gc_malloc(64) : String
        sprintf(str, "%Lu", this)
        str
    }
    
    isOdd:  func -> Bool { this % 2 == 1 }
    isEven: func -> Bool { this % 2 == 0 }
    
    in: func(range: Range) -> Bool {
        return this >= range min && this < range max
    }
    
}

operator as (value: UInt) -> String {
    value toString()
}

operator as (value: Short) -> String {
    value toString()
}

operator as (value: UShort) -> String {
    value toString()
}

operator as (value: Long) -> String {
    value toString()
}

operator as (value: ULong) -> String {
    value toString()
}

operator as (value: ULLong) -> String {
    value toString()
}

/**
 * fixed-size integer types
 */
Int8: cover from int8_t extends LLong
Int16: cover from int16_t extends LLong
Int32: cover from int32_t extends LLong
Int64: cover from int64_t extends LLong

UInt8:  cover from uint8_t extends ULLong
UInt16: cover from uint16_t extends ULLong
UInt32: cover from uint32_t extends ULLong
UInt64: cover from uint64_t extends ULLong

Octet: cover from UInt8 extends LLong

SizeT: cover from size_t extends LLong
PtrDiffT: cover from ptrdiff_t extends LLong

Bool: cover from bool {
    
    toString: func -> String { return this ? "true" : "false" }
    
}

operator as (value: Int8) -> String {
    value toString()
}

operator as (value: Int16) -> String {
    value toString()
}

operator as (value: Int32) -> String {
    value toString()
}

operator as (value: Int64) -> String {
    value toString()
}

operator as (value: UInt8) -> String {
    value toString()
}

operator as (value: UInt16) -> String {
    value toString()
}

operator as (value: UInt32) -> String {
    value toString()
}

operator as (value: UInt64) -> String {
    value toString()
}

operator as (value: Octet) -> String {
    value toString()
}

operator as (value: SizeT) -> String {
    value toString()
}

operator as (value: PtrDiffT) -> String {
    value toString()
}

operator as (value: Bool) -> String {
    value toString()
}

/**
 * real types
 */
Float: cover from float extends Double {}
Double: cover from double extends LDouble

LDouble: cover from long double {
    
    toString: func -> String {
        str = gc_malloc(64) : String
        sprintf(str, "%.2Lf", this)
        str
    }
    
    abs: func -> This {
        return this < 0 ? -this : this
    }
    
}

operator as (value: Float) -> String {
    value toString()
}

operator as (value: Double) -> String {
    value toString()
}

operator as (value: LDouble) -> String {
    value toString()
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
    
    inheritsFrom: final func (T: Class) -> Bool {
        if(this == T) return true
        return (super ? super as This inheritsFrom(T) : false)
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

    instanceOf: final func (T: Class) -> Bool {
        class inheritsFrom(T)
    }
    
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

    /**
      * @return the contents of the iterable as ArrayList.
      */
    toArrayList: func -> ArrayList<T> {
        result := ArrayList<T> new()
        for(elem: T in this) {
            result add(elem)
        }
        result
    }
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


/**
 * iterators
 */

StringIterator: class <T> extends Iterator<T> {
    
    i := 0
    str: String
    
    init: func (=str) {}
    
    hasNext: func -> Bool {
        i < str length()
    }
    
    next: func -> T {
        c := str[i]
        i += 1
        return c
    }
    
    hasPrev: func -> Bool {
        i > 0
    }
    
    prev: func -> T {
        i -= 1
        return str[i]
    }
    
    remove: func -> Bool { false } // this could be implemented!
    
}

None: class {init: func {}}

Cell: class <T> {
    val :T
    init: func(=val) {}
}

