#ifndef example1_h
#define example1_h

struct MyObject;

#include <stdio.h>
#include <stdlib.h>
#include "OocLib.h"

typedef struct MyObject__class {
	String name;
	Void (*__sayHello)(struct MyObject* );
	
}* MyObject__class;


typedef struct MyObject {
	MyObject__class class;
	String name;
	
}* MyObject;


extern MyObject__class MyObject__classInstance;
extern MyObject __MyObject_new_String(String);
Void __MyObject_sayHello(struct MyObject* );
Int main();


#endif // example1_h
