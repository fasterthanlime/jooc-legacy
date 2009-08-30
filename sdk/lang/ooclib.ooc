include stdlib, stdio, stdint, stdbool, memory, gc/gc, string

// character and pointer types
Char: cover from char
UChar: cover from unsigned char
String: cover from Char*
Pointer: cover from void*

// variable-size integer types
Int: cover from int
UInt: cover from unsigned int
Short: cover from short
UShort: cover from unsigned short
Long: cover from long
ULong: cover from unsigned long
LLong: cover from long long
ULLong: cover from unsigned long long

// floating-point types
Float: cover from float
Double: cover from double
LDouble: cover from long double

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
void: extern cover
Bool: cover from bool
SizeT: cover from size_t

sizeof: extern func (...) -> SizeT
memcpy: extern func (Pointer, Pointer, SizeT)
scanf: extern func (String, ...)
printf: extern func (String, ...)
sprintf: extern func (String, String, ...)

fprintf: extern func (Stream, String, ...)
Stream: cover from Int
stdout, stderr, stdin : extern Stream

println: func (str: String) {
	printf("%s\n", str)
}
println: func ~empty {
	printf("\n")
}

gc_malloc: extern(GC_MALLOC) func (size: SizeT) -> Pointer
gc_malloc_atomic: extern(GC_MALLOC_ATOMIC) func (size: SizeT) -> Pointer
gc_realloc: extern(GC_REALLOC) func (ptr: Pointer, size: SizeT) -> Pointer
gc_calloc: func (nmemb: SizeT, size: SizeT) -> Pointer {
	gc_malloc(nmemb * size)
}
