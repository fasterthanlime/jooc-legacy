#ifndef gtk_Container_h
#define gtk_Container_h

struct gtk_Container;

#include <gtk/gtk.h>
#include "Widget.h"
#include "../OocLib.h"

typedef struct gtk_Container__class {
	String name;
	GtkObject * (*__getObject)(struct gtk_Container* );
	Void (*__connect_String_Func)(struct gtk_Container* , String, Func);
	Void (*__connect_String_Func_GPointer)(struct gtk_Container* , String, Func, GPointer);
	Void (*__connectNaked_String_Func)(struct gtk_Container* , String, Func);
	Void (*__connectNaked_String_Func_GPointer)(struct gtk_Container* , String, Func, GPointer);
	Void (*__emitByName_String)(struct gtk_Container* , String);
	Void (*__ref)(struct gtk_Container* );
	Void (*__unref)(struct gtk_Container* );
	Void (*__sink)(struct gtk_Container* );
	Void (*__setProperty_String_GValue__star)(struct gtk_Container* , String, GValue *);
	GtkWidget * (*__getWidget)(struct gtk_Container* );
	Void (*__setSensitive_Bool)(struct gtk_Container* , Bool);
	Bool (*__isRealized)(struct gtk_Container* );
	Void (*__realize)(struct gtk_Container* );
	Void (*__forceRepaint_Bool)(struct gtk_Container* , Bool);
	Void (*__show)(struct gtk_Container* );
	Void (*__showAll)(struct gtk_Container* );
	Void (*__hide)(struct gtk_Container* );
	Void (*__destroy)(struct gtk_Container* );
	Void (*__setPosition_gint_gint)(struct gtk_Container* , gint, gint);
	Void (*__setUSize_gint_gint)(struct gtk_Container* , gint, gint);
	Void (*__setEvents_Int)(struct gtk_Container* , Int);
	GtkAllocation (*__getAllocation)(struct gtk_Container* );
	Int (*__getWidth)(struct gtk_Container* );
	Int (*__getHeight)(struct gtk_Container* );
	struct gtk_Style*  (*__getStyle)(struct gtk_Container* );
	Void (*__add_Widget)(struct gtk_Container* , struct gtk_Widget* );
	Void (*__remove_Widget)(struct gtk_Container* , struct gtk_Widget* );
	Void (*__setBorderWidth_Int)(struct gtk_Container* , Int);
	
}* gtk_Container__class;


typedef struct gtk_Container {
	gtk_Container__class class;
	
}* gtk_Container;


extern gtk_Container__class gtk_Container__classInstance;
GtkObject * __gtk_Container_getObject(struct gtk_Container* );
Void __gtk_Container_connect_String_Func(struct gtk_Container* , String, Func);
Void __gtk_Container_connect_String_Func_GPointer(struct gtk_Container* , String, Func, GPointer);
Void __gtk_Container_connectNaked_String_Func(struct gtk_Container* , String, Func);
Void __gtk_Container_connectNaked_String_Func_GPointer(struct gtk_Container* , String, Func, GPointer);
Void __gtk_Container_emitByName_String(struct gtk_Container* , String);
Void __gtk_Container_ref(struct gtk_Container* );
Void __gtk_Container_unref(struct gtk_Container* );
Void __gtk_Container_sink(struct gtk_Container* );
Void __gtk_Container_setProperty_String_GValue__star(struct gtk_Container* , String, GValue *);
GtkWidget * __gtk_Container_getWidget(struct gtk_Container* );
Void __gtk_Container_setSensitive_Bool(struct gtk_Container* , Bool);
Bool __gtk_Container_isRealized(struct gtk_Container* );
Void __gtk_Container_realize(struct gtk_Container* );
Void __gtk_Container_forceRepaint_Bool(struct gtk_Container* , Bool);
Void __gtk_Container_show(struct gtk_Container* );
Void __gtk_Container_showAll(struct gtk_Container* );
Void __gtk_Container_hide(struct gtk_Container* );
Void __gtk_Container_destroy(struct gtk_Container* );
Void __gtk_Container_setPosition_gint_gint(struct gtk_Container* , gint, gint);
Void __gtk_Container_setUSize_gint_gint(struct gtk_Container* , gint, gint);
Void __gtk_Container_setEvents_Int(struct gtk_Container* , Int);
GtkAllocation __gtk_Container_getAllocation(struct gtk_Container* );
Int __gtk_Container_getWidth(struct gtk_Container* );
Int __gtk_Container_getHeight(struct gtk_Container* );
struct gtk_Style*  __gtk_Container_getStyle(struct gtk_Container* );
Void __gtk_Container_add_Widget(struct gtk_Container* , struct gtk_Widget* );
Void __gtk_Container_remove_Widget(struct gtk_Container* , struct gtk_Widget* );
Void __gtk_Container_setBorderWidth_Int(struct gtk_Container* , Int);


#endif // gtk_Container_h
