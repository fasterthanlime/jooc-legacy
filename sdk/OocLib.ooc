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

unmangled func GC_calloc(size_t nmemb, size_t size) -> Object {
	Object tmp = GC_malloc(nmemb * size);
	memset(tmp, 0, nmemb * size);
	return tmp;
}
