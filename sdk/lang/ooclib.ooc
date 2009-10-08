include stdlib, stdint, stdbool, math, stdarg, memory, gc/gc, string

// character and pointer types
Char: cover from char
UChar: cover from unsigned char
WChar: cover from wchar_t
String: cover from Char*
Pointer: cover from void*

// variable-size integer types
Int: cover from int
UInt: cover from unsigned int extends LLong
Short: cover from short extends LLong
UShort: cover from unsigned short extends LLong
Long: cover from long extends LLong
ULong: cover from unsigned long extends LLong
LLong: cover from long long
ULLong: cover from unsigned long long extends LLong

// floating-point types
LDouble: cover from long double
Double: cover from double
Float: cover from float extends Double

// fixed-size integer types
Int8: cover from int8_t
Int16: cover from int16_t
Int32: cover from int32_t
Int64: cover from int64_t

UInt8:  cover from uint8_t
UInt16: cover from uint16_t
UInt32: cover from uint32_t
UInt64: cover from uint64_t

Octet: cover from UInt8

// other types
Void: cover from void
Bool: cover from bool
SizeT: cover from size_t extends LLong

// variable arguments
VaList: cover from va_list
va_start: extern func (VaList, ...) // ap, last_arg
va_arg: extern func (VaList, ...) // ap, type
va_end: extern func (VaList) // ap

exit: extern func (Int)

// math
cos: extern func (Double) -> Double
sin: extern func (Double) -> Double
tan: extern func (Double) -> Double

acos: extern func (Double) -> Double
asin: extern func (Double) -> Double
atan: extern func (Double) -> Double

atan2: extern func (Double, Double) -> Double

sqrt: extern func (Double) -> Double
pow: extern func (Double, Double) -> Double

srand: extern func(Int)
rand: extern func -> Int

// memory management
sizeof: extern func (...) -> SizeT
memset: extern func (Pointer, Int, SizeT) -> Pointer
memcmp: extern func (Pointer, Pointer, SizeT) -> Int
memmove: extern func (Pointer, Pointer, SizeT)
memcpy: extern func (Pointer, Pointer, SizeT)

gc_malloc: extern(GC_MALLOC) func (size: SizeT) -> Pointer
gc_malloc_atomic: extern(GC_MALLOC_ATOMIC) func (size: SizeT) -> Pointer
gc_realloc: extern(GC_REALLOC) func (ptr: Pointer, size: SizeT) -> Pointer
gc_calloc: func (nmemb: SizeT, size: SizeT) -> Pointer {
	gc_malloc(nmemb * size)
}
