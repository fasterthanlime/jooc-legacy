#ifndef OocLib_h
#define OocLib_h


#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
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
typedef long double LDouble;
typedef short Short;
typedef long Long;
typedef long long LLong;
typedef void Void;
typedef int (*Func)();
typedef size_t Size;
typedef uint8_t Octet;
Object GC_calloc(Int, Size);


#endif // OocLib_h
