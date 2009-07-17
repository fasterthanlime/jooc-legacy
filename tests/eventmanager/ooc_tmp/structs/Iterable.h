#ifndef structs_Iterable_h
#define structs_Iterable_h

struct structs_Iterable;

#include "Iterator.h"
#include "../OocLib.h"

typedef struct structs_Iterable__class {
	String name;
	String simpleName;
	struct structs_Iterator*  (*__iterator)(struct structs_Iterable* );
	
}* structs_Iterable__class;


typedef struct structs_Iterable {
	structs_Iterable__class class;
	
}* structs_Iterable;


extern structs_Iterable__class structs_Iterable__classInstance;
struct structs_Iterator*  __structs_Iterable_iterator(struct structs_Iterable* );


#endif // structs_Iterable_h
