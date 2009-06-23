#ifndef minimal_h
#define minimal_h

struct Blah;

#include "OocLib.h"

typedef struct Blah__class {
	String name;
	
}* Blah__class;


typedef struct Blah {
	Blah__class class;
	struct Blah*  b;
	
}* Blah;


extern Blah__class Blah__classInstance;
extern Blah __Blah_new();
Int main();


#endif // minimal_h
