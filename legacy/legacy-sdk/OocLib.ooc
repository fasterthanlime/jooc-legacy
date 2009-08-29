include stdlib, stdio, stdint, stdbool, memory, gc/gc;

ctype uint8_t;

typedef char* String;
typedef void* Object;
typedef bool Bool;
typedef char Char;
typedef int Int;
typedef unsigned int UInt;
typedef float Float;
typedef double Double;
typedef long double LDouble;
typedef short Short;
typedef long Long;
typedef long long LLong;
typedef void Void;
typedef int (*Func)();
typedef size_t Size;
typedef uint8_t Octet;

unmangled func GC_calloc(Int nmemb, Size size) -> Object {
	Size memsize = nmemb * size;
	Object tmp = GC_malloc(memsize);
	memset(tmp, 0, memsize);
	return tmp;
}
