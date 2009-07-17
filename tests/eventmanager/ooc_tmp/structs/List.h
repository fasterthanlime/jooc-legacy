#ifndef structs_List_h
#define structs_List_h

struct structs_List;

#include "Iterable.h"
#include "../OocLib.h"
#include "Iterator.h"

typedef struct structs_List__class {
	String name;
	String simpleName;
	struct structs_Iterator*  (*__iterator)(struct structs_List* );
	Void (*__add_Object)(struct structs_List* , Object);
	Void (*__add_Int_Object)(struct structs_List* , Int, Object);
	Void (*__addAll_List)(struct structs_List* , struct structs_List* );
	Void (*__addAll_Int_List)(struct structs_List* , Int, struct structs_List* );
	Void (*__clear)(struct structs_List* );
	Bool (*__removeLast)(struct structs_List* );
	Bool (*__contains_Object)(struct structs_List* , Object);
	Object (*__get_Int)(struct structs_List* , Int);
	Int (*__indexOf_Object)(struct structs_List* , Object);
	Bool (*__isEmpty)(struct structs_List* );
	Int (*__lastIndexOf_Object)(struct structs_List* , Object);
	Object (*__remove_Int)(struct structs_List* , Int);
	Bool (*__removeElement_Object)(struct structs_List* , Object);
	Void (*__set_Int_Object)(struct structs_List* , Int, Object);
	Int (*__size)(struct structs_List* );
	Int (*__capacity)(struct structs_List* );
	
}* structs_List__class;


typedef struct structs_List {
	structs_List__class class;
	
}* structs_List;


extern structs_List__class structs_List__classInstance;
struct structs_Iterator*  __structs_List_iterator(struct structs_List* );
Void __structs_List_add_Object(struct structs_List* , Object);
Void __structs_List_add_Int_Object(struct structs_List* , Int, Object);
Void __structs_List_addAll_List(struct structs_List* , struct structs_List* );
Void __structs_List_addAll_Int_List(struct structs_List* , Int, struct structs_List* );
Void __structs_List_clear(struct structs_List* );
Bool __structs_List_removeLast(struct structs_List* );
Bool __structs_List_contains_Object(struct structs_List* , Object);
Object __structs_List_get_Int(struct structs_List* , Int);
Int __structs_List_indexOf_Object(struct structs_List* , Object);
Bool __structs_List_isEmpty(struct structs_List* );
Int __structs_List_lastIndexOf_Object(struct structs_List* , Object);
Object __structs_List_remove_Int(struct structs_List* , Int);
Bool __structs_List_removeElement_Object(struct structs_List* , Object);
Void __structs_List_set_Int_Object(struct structs_List* , Int, Object);
Int __structs_List_size(struct structs_List* );
Int __structs_List_capacity(struct structs_List* );


#endif // structs_List_h
