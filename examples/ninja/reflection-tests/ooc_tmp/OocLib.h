#ifndef OocLib_h
#define OocLib_h


#include <stdlib.h>
#include <stdio.h>
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
typedef size_t SizeT;
Object GC_calloc(Int, SizeT);


#endif // OocLib_h
