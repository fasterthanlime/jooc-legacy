#ifndef gtk_Window_h
#define gtk_Window_h

struct gtk_Window;

#include <gtk/gtk.h>
#include "Container.h"
#include "../OocLib.h"

typedef struct gtk_Window__class {
	String name;
	GtkObject * (*__getObject)(struct gtk_Window* );
	Void (*__connect_String_Func)(struct gtk_Window* , String, Func);
	Void (*__connect_String_Func_GPointer)(struct gtk_Window* , String, Func, GPointer);
	Void (*__connectNaked_String_Func)(struct gtk_Window* , String, Func);
	Void (*__connectNaked_String_Func_GPointer)(struct gtk_Window* , String, Func, GPointer);
	Void (*__emitByName_String)(struct gtk_Window* , String);
	Void (*__ref)(struct gtk_Window* );
	Void (*__unref)(struct gtk_Window* );
	Void (*__sink)(struct gtk_Window* );
	Void (*__setProperty_String_GValue__star)(struct gtk_Window* , String, GValue *);
	GtkWidget * (*__getWidget)(struct gtk_Window* );
	Void (*__setSensitive_Bool)(struct gtk_Window* , Bool);
	Bool (*__isRealized)(struct gtk_Window* );
	Void (*__realize)(struct gtk_Window* );
	Void (*__forceRepaint_Bool)(struct gtk_Window* , Bool);
	Void (*__show)(struct gtk_Window* );
	Void (*__showAll)(struct gtk_Window* );
	Void (*__hide)(struct gtk_Window* );
	Void (*__destroy)(struct gtk_Window* );
	Void (*__setPosition_gint_gint)(struct gtk_Window* , gint, gint);
	Void (*__setUSize_gint_gint)(struct gtk_Window* , gint, gint);
	Void (*__setEvents_Int)(struct gtk_Window* , Int);
	GtkAllocation (*__getAllocation)(struct gtk_Window* );
	Int (*__getWidth)(struct gtk_Window* );
	Int (*__getHeight)(struct gtk_Window* );
	struct gtk_Style*  (*__getStyle)(struct gtk_Window* );
	Void (*__add_Widget)(struct gtk_Window* , struct gtk_Widget* );
	Void (*__remove_Widget)(struct gtk_Window* , struct gtk_Widget* );
	Void (*__setBorderWidth_Int)(struct gtk_Window* , Int);
	Void (*__setTitle_String)(struct gtk_Window* , String);
	
}* gtk_Window__class;


typedef struct gtk_Window {
	gtk_Window__class class;
	GtkWindow * window;
	
}* gtk_Window;


extern gtk_Window__class gtk_Window__classInstance;
GtkObject * __gtk_Window_getObject(struct gtk_Window* );
Void __gtk_Window_connect_String_Func(struct gtk_Window* , String, Func);
Void __gtk_Window_connect_String_Func_GPointer(struct gtk_Window* , String, Func, GPointer);
Void __gtk_Window_connectNaked_String_Func(struct gtk_Window* , String, Func);
Void __gtk_Window_connectNaked_String_Func_GPointer(struct gtk_Window* , String, Func, GPointer);
Void __gtk_Window_emitByName_String(struct gtk_Window* , String);
Void __gtk_Window_ref(struct gtk_Window* );
Void __gtk_Window_unref(struct gtk_Window* );
Void __gtk_Window_sink(struct gtk_Window* );
Void __gtk_Window_setProperty_String_GValue__star(struct gtk_Window* , String, GValue *);
GtkWidget * __gtk_Window_getWidget(struct gtk_Window* );
Void __gtk_Window_setSensitive_Bool(struct gtk_Window* , Bool);
Bool __gtk_Window_isRealized(struct gtk_Window* );
Void __gtk_Window_realize(struct gtk_Window* );
Void __gtk_Window_forceRepaint_Bool(struct gtk_Window* , Bool);
Void __gtk_Window_show(struct gtk_Window* );
Void __gtk_Window_showAll(struct gtk_Window* );
Void __gtk_Window_hide(struct gtk_Window* );
Void __gtk_Window_destroy(struct gtk_Window* );
Void __gtk_Window_setPosition_gint_gint(struct gtk_Window* , gint, gint);
Void __gtk_Window_setUSize_gint_gint(struct gtk_Window* , gint, gint);
Void __gtk_Window_setEvents_Int(struct gtk_Window* , Int);
GtkAllocation __gtk_Window_getAllocation(struct gtk_Window* );
Int __gtk_Window_getWidth(struct gtk_Window* );
Int __gtk_Window_getHeight(struct gtk_Window* );
struct gtk_Style*  __gtk_Window_getStyle(struct gtk_Window* );
Void __gtk_Window_add_Widget(struct gtk_Window* , struct gtk_Widget* );
Void __gtk_Window_remove_Widget(struct gtk_Window* , struct gtk_Widget* );
Void __gtk_Window_setBorderWidth_Int(struct gtk_Window* , Int);
extern gtk_Window __gtk_Window_new();
extern gtk_Window __gtk_Window_new_String(String);
Void __gtk_Window_setTitle_String(struct gtk_Window* , String);


#endif // gtk_Window_h
