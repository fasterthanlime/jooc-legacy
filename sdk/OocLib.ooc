include stdbool, memory, gc/gc;

typedef char* String;
typedef void* Object;
typedef bool Bool;
typedef char Char;
typedef int Int;
typedef unsigned int UInt;
typedef float Float;
typedef double Double;
typedef short Short;
typedef long Long;
typedef long long LLong;
typedef void Void;
typedef int (*Func)();
typedef size_t SizeT;

unmangled func GC_calloc(Int nmemb, SizeT size) -> Object {
	SizeT memsize = nmemb * size;
	Object tmp = GC_malloc(memsize);
	memset(tmp, 0, memsize);
	return tmp;
}
