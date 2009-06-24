#ifndef OocLib_h
#define OocLib_h


#include <stdbool.h>
#include <memory.h>
#include <gc/gc.h>
typedef char *String;
typedef void *Object;
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
Object GC_calloc(size_t, size_t);


#endif // OocLib_h
