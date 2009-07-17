#ifndef event_Event_h
#define event_Event_h

struct event_Event;

#include "../OocLib.h"

typedef struct event_Event__class {
	String name;
	String simpleName;
	
}* event_Event__class;


typedef struct event_Event {
	event_Event__class class;
	Int type;
	Object target;
	
}* event_Event;


extern event_Event__class event_Event__classInstance;
extern event_Event __event_Event_new_Int(Int);


#endif // event_Event_h
