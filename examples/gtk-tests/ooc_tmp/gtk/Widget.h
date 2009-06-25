#ifndef gtk_Widget_h
#define gtk_Widget_h

struct gtk_Widget;

#include <stdio.h>
#include <gtk/gtk.h>
#include "Gtk.h"
#include "Style.h"
#include "GObject.h"
#include "../OocLib.h"

typedef struct gtk_Widget__class {
	String name;
	GtkObject * (*__getObject)(struct gtk_Widget* );
	Void (*__connect_String_Func)(struct gtk_Widget* , String, Func);
	Void (*__connect_String_Func_GPointer)(struct gtk_Widget* , String, Func, GPointer);
	Void (*__connectNaked_String_Func)(struct gtk_Widget* , String, Func);
	Void (*__connectNaked_String_Func_GPointer)(struct gtk_Widget* , String, Func, GPointer);
	Void (*__emitByName_String)(struct gtk_Widget* , String);
	Void (*__ref)(struct gtk_Widget* );
	Void (*__unref)(struct gtk_Widget* );
	Void (*__sink)(struct gtk_Widget* );
	Void (*__setProperty_String_GValue__star)(struct gtk_Widget* , String, GValue *);
	GtkWidget * (*__getWidget)(struct gtk_Widget* );
	Void (*__setSensitive_Bool)(struct gtk_Widget* , Bool);
	Bool (*__isRealized)(struct gtk_Widget* );
	Void (*__realize)(struct gtk_Widget* );
	Void (*__forceRepaint_Bool)(struct gtk_Widget* , Bool);
	Void (*__show)(struct gtk_Widget* );
	Void (*__showAll)(struct gtk_Widget* );
	Void (*__hide)(struct gtk_Widget* );
	Void (*__destroy)(struct gtk_Widget* );
	Void (*__setPosition_gint_gint)(struct gtk_Widget* , gint, gint);
	Void (*__setUSize_gint_gint)(struct gtk_Widget* , gint, gint);
	Void (*__setEvents_Int)(struct gtk_Widget* , Int);
	GtkAllocation (*__getAllocation)(struct gtk_Widget* );
	Int (*__getWidth)(struct gtk_Widget* );
	Int (*__getHeight)(struct gtk_Widget* );
	struct gtk_Style*  (*__getStyle)(struct gtk_Widget* );
	
}* gtk_Widget__class;


typedef struct gtk_Widget {
	gtk_Widget__class class;
	
}* gtk_Widget;


extern gtk_Widget__class gtk_Widget__classInstance;
GtkObject * __gtk_Widget_getObject(struct gtk_Widget* );
Void __gtk_Widget_connect_String_Func(struct gtk_Widget* , String, Func);
Void __gtk_Widget_connect_String_Func_GPointer(struct gtk_Widget* , String, Func, GPointer);
Void __gtk_Widget_connectNaked_String_Func(struct gtk_Widget* , String, Func);
Void __gtk_Widget_connectNaked_String_Func_GPointer(struct gtk_Widget* , String, Func, GPointer);
Void __gtk_Widget_emitByName_String(struct gtk_Widget* , String);
Void __gtk_Widget_ref(struct gtk_Widget* );
Void __gtk_Widget_unref(struct gtk_Widget* );
Void __gtk_Widget_sink(struct gtk_Widget* );
Void __gtk_Widget_setProperty_String_GValue__star(struct gtk_Widget* , String, GValue *);
GtkWidget * __gtk_Widget_getWidget(struct gtk_Widget* );
Void __gtk_Widget_setSensitive_Bool(struct gtk_Widget* , Bool);
Bool __gtk_Widget_isRealized(struct gtk_Widget* );
Void __gtk_Widget_realize(struct gtk_Widget* );
Void __gtk_Widget_forceRepaint_Bool(struct gtk_Widget* , Bool);
Void __gtk_Widget_show(struct gtk_Widget* );
Void __gtk_Widget_showAll(struct gtk_Widget* );
Void __gtk_Widget_hide(struct gtk_Widget* );
Void __gtk_Widget_destroy(struct gtk_Widget* );
Void __gtk_Widget_setPosition_gint_gint(struct gtk_Widget* , gint, gint);
Void __gtk_Widget_setUSize_gint_gint(struct gtk_Widget* , gint, gint);
Void __gtk_Widget_setEvents_Int(struct gtk_Widget* , Int);
GtkAllocation __gtk_Widget_getAllocation(struct gtk_Widget* );
Int __gtk_Widget_getWidth(struct gtk_Widget* );
Int __gtk_Widget_getHeight(struct gtk_Widget* );
struct gtk_Style*  __gtk_Widget_getStyle(struct gtk_Widget* );


#endif // gtk_Widget_h
