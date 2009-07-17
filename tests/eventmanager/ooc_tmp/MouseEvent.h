#ifndef MouseEvent_h
#define MouseEvent_h

struct MouseEvent;

#include "event/Event.h"
#include "OocLib.h"

typedef struct MouseEvent__class {
	String name;
	String simpleName;
	
}* MouseEvent__class;


typedef struct MouseEvent {
	MouseEvent__class class;
	Int type;
	Object target;
	Int x;
	Int y;
	
}* MouseEvent;


extern Int MouseEvent_CLIC;
extern Int MouseEvent_OVER;
extern MouseEvent__class MouseEvent__classInstance;
extern MouseEvent __MouseEvent_new_Int(Int);
extern MouseEvent __MouseEvent_new_Int_Int_Int(Int, Int, Int);


#endif // MouseEvent_h
