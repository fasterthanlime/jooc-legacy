#ifndef event_EventDispatcher_h
#define event_EventDispatcher_h

struct event_EventDispatcher;

struct event_EventListener;

#include "../structs/SparseList.h"
#include "../OocLib.h"
#include "Event.h"

typedef struct event_EventDispatcher__class {
	String name;
	String simpleName;
	Void (*__addEventListener_Func_Int)(struct event_EventDispatcher* , Func, Int);
	Bool (*__removeEventListener_Func_Int)(struct event_EventDispatcher* , Func, Int);
	Void (*__dispatchEvent_Event)(struct event_EventDispatcher* , struct event_Event* );
	
}* event_EventDispatcher__class;


typedef struct event_EventDispatcher {
	event_EventDispatcher__class class;
	struct structs_SparseList*  listeners;
	
}* event_EventDispatcher;


extern event_EventDispatcher__class event_EventDispatcher__classInstance;
Void __event_EventDispatcher_addEventListener_Func_Int(struct event_EventDispatcher* , Func, Int);
Bool __event_EventDispatcher_removeEventListener_Func_Int(struct event_EventDispatcher* , Func, Int);
Void __event_EventDispatcher_dispatchEvent_Event(struct event_EventDispatcher* , struct event_Event* );


typedef struct event_EventListener__class {
	String name;
	String simpleName;
	
}* event_EventListener__class;



typedef struct event_EventListener {
	event_EventListener__class class;
	Func listener;
	Int eventType;
	
}* event_EventListener;


extern event_EventListener__class event_EventListener__classInstance;
extern event_EventListener __event_EventListener_new_Func_Int(Func, Int);


#endif // event_EventDispatcher_h
