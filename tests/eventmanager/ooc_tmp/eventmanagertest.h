#ifndef eventmanagertest_h
#define eventmanagertest_h

struct TestObject;

#include "event/EventDispatcher.h"
#include "OocLib.h"
#include "MouseEvent.h"

typedef struct TestObject__class {
	String name;
	String simpleName;
	Void (*__addEventListener_Func_Int)(struct TestObject* , Func, Int);
	Bool (*__removeEventListener_Func_Int)(struct TestObject* , Func, Int);
	Void (*__dispatchEvent_Event)(struct TestObject* , struct event_Event* );
	Void (*__clic)(struct TestObject* );
	Void (*__over)(struct TestObject* );
	
}* TestObject__class;


typedef struct TestObject {
	TestObject__class class;
	struct structs_SparseList*  listeners;
	String obj_name;
	
}* TestObject;


extern TestObject__class TestObject__classInstance;
extern TestObject __TestObject_new();
Void __TestObject_addEventListener_Func_Int(struct TestObject* , Func, Int);
Bool __TestObject_removeEventListener_Func_Int(struct TestObject* , Func, Int);
Void __TestObject_dispatchEvent_Event(struct TestObject* , struct event_Event* );
extern TestObject __TestObject_new_String(String);
Void __TestObject_clic(struct TestObject* );
Void __TestObject_over(struct TestObject* );
Int main();
Void __cool_MouseEvent(struct MouseEvent* );


#endif // eventmanagertest_h
