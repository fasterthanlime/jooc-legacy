#ifndef structs_SparseList_h
#define structs_SparseList_h

struct structs_SparseList;

struct structs_SparseListIterator;

#include <stdlib.h>
#include <stdio.h>
#include <memory.h>
#include "List.h"
#include "Iterator.h"
#include "Iterable.h"
#include "../OocLib.h"

typedef struct structs_SparseList__class {
	String name;
	String simpleName;
	struct structs_Iterator*  (*__iterator)(struct structs_SparseList* );
	Void (*__add_Object)(struct structs_SparseList* , Object);
	Void (*__add_Int_Object)(struct structs_SparseList* , Int, Object);
	Void (*__addAll_List)(struct structs_SparseList* , struct structs_List* );
	Void (*__addAll_Int_List)(struct structs_SparseList* , Int, struct structs_List* );
	Void (*__clear)(struct structs_SparseList* );
	Bool (*__removeLast)(struct structs_SparseList* );
	Bool (*__contains_Object)(struct structs_SparseList* , Object);
	Object (*__get_Int)(struct structs_SparseList* , Int);
	Int (*__indexOf_Object)(struct structs_SparseList* , Object);
	Bool (*__isEmpty)(struct structs_SparseList* );
	Int (*__lastIndexOf_Object)(struct structs_SparseList* , Object);
	Object (*__remove_Int)(struct structs_SparseList* , Int);
	Bool (*__removeElement_Object)(struct structs_SparseList* , Object);
	Void (*__set_Int_Object)(struct structs_SparseList* , Int, Object);
	Int (*__size)(struct structs_SparseList* );
	Int (*__capacity)(struct structs_SparseList* );
	Int (*__slotOf_Object)(struct structs_SparseList* , Object);
	Int (*__getFreeSlot_Int)(struct structs_SparseList* , Int);
	Int (*__indexToSlot_Int)(struct structs_SparseList* , Int);
	Void (*__grow)(struct structs_SparseList* );
	Bool (*__isValidIndex_Int)(struct structs_SparseList* , Int);
	
}* structs_SparseList__class;


typedef struct structs_SparseList {
	structs_SparseList__class class;
	Object * data;
	Int capacity;
	Int size;
	
}* structs_SparseList;


extern structs_SparseList__class structs_SparseList__classInstance;
struct structs_Iterator*  __structs_SparseList_iterator(struct structs_SparseList* );
Void __structs_SparseList_add_Object(struct structs_SparseList* , Object);
Void __structs_SparseList_add_Int_Object(struct structs_SparseList* , Int, Object);
Void __structs_SparseList_addAll_List(struct structs_SparseList* , struct structs_List* );
Void __structs_SparseList_addAll_Int_List(struct structs_SparseList* , Int, struct structs_List* );
Void __structs_SparseList_clear(struct structs_SparseList* );
Bool __structs_SparseList_removeLast(struct structs_SparseList* );
Bool __structs_SparseList_contains_Object(struct structs_SparseList* , Object);
Object __structs_SparseList_get_Int(struct structs_SparseList* , Int);
Int __structs_SparseList_indexOf_Object(struct structs_SparseList* , Object);
Bool __structs_SparseList_isEmpty(struct structs_SparseList* );
Int __structs_SparseList_lastIndexOf_Object(struct structs_SparseList* , Object);
Object __structs_SparseList_remove_Int(struct structs_SparseList* , Int);
Bool __structs_SparseList_removeElement_Object(struct structs_SparseList* , Object);
Void __structs_SparseList_set_Int_Object(struct structs_SparseList* , Int, Object);
Int __structs_SparseList_size(struct structs_SparseList* );
Int __structs_SparseList_capacity(struct structs_SparseList* );
extern structs_SparseList __structs_SparseList_new();
extern structs_SparseList __structs_SparseList_new_Int(Int);
Int __structs_SparseList_slotOf_Object(struct structs_SparseList* , Object);
Int __structs_SparseList_getFreeSlot_Int(struct structs_SparseList* , Int);
Int __structs_SparseList_indexToSlot_Int(struct structs_SparseList* , Int);
Void __structs_SparseList_grow(struct structs_SparseList* );
Bool __structs_SparseList_isValidIndex_Int(struct structs_SparseList* , Int);


typedef struct structs_SparseListIterator__class {
	String name;
	String simpleName;
	Bool (*__hasNext)(struct structs_SparseListIterator* );
	Object (*__next)(struct structs_SparseListIterator* );
	
}* structs_SparseListIterator__class;



typedef struct structs_SparseListIterator {
	structs_SparseListIterator__class class;
	struct structs_SparseList*  list;
	Int index;
	Int slot;
	
}* structs_SparseListIterator;


extern structs_SparseListIterator__class structs_SparseListIterator__classInstance;
Bool __structs_SparseListIterator_hasNext(struct structs_SparseListIterator* );
Object __structs_SparseListIterator_next(struct structs_SparseListIterator* );
extern structs_SparseListIterator __structs_SparseListIterator_new_SparseList(struct structs_SparseList* );


#endif // structs_SparseList_h
