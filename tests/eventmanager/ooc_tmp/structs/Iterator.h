#ifndef structs_Iterator_h
#define structs_Iterator_h

struct structs_Iterator;

#include "../OocLib.h"

typedef struct structs_Iterator__class {
	String name;
	String simpleName;
	Bool (*__hasNext)(struct structs_Iterator* );
	Object (*__next)(struct structs_Iterator* );
	
}* structs_Iterator__class;


typedef struct structs_Iterator {
	structs_Iterator__class class;
	
}* structs_Iterator;


extern structs_Iterator__class structs_Iterator__classInstance;
Bool __structs_Iterator_hasNext(struct structs_Iterator* );
Object __structs_Iterator_next(struct structs_Iterator* );


#endif // structs_Iterator_h
