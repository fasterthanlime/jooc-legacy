#ifndef static_pointer_h
#define static_pointer_h

struct Blah;

#include <stdio.h>
#include "OocLib.h"

typedef struct Blah__class {
	String name;
	
}* Blah__class;


typedef struct Blah {
	Blah__class class;
	
}* Blah;


extern Func Blah_blih;
extern Blah__class Blah__classInstance;
extern Void __Blah_bloh();
extern Blah __Blah_new();
Int main();


#endif // static_pointer_h
