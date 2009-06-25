#ifndef gtk_TreeView_h
#define gtk_TreeView_h

struct gtk_TreeView;

#include <gtk/gtk.h>
#include "Widget.h"
#include "TreeViewColumn.h"
#include "TreeModel.h"
#include "../OocLib.h"

typedef struct gtk_TreeView__class {
	String name;
	GtkObject * (*__getObject)(struct gtk_TreeView* );
	Void (*__connect_String_Func)(struct gtk_TreeView* , String, Func);
	Void (*__connect_String_Func_GPointer)(struct gtk_TreeView* , String, Func, GPointer);
	Void (*__connectNaked_String_Func)(struct gtk_TreeView* , String, Func);
	Void (*__connectNaked_String_Func_GPointer)(struct gtk_TreeView* , String, Func, GPointer);
	Void (*__emitByName_String)(struct gtk_TreeView* , String);
	Void (*__ref)(struct gtk_TreeView* );
	Void (*__unref)(struct gtk_TreeView* );
	Void (*__sink)(struct gtk_TreeView* );
	Void (*__setProperty_String_GValue__star)(struct gtk_TreeView* , String, GValue *);
	GtkWidget * (*__getWidget)(struct gtk_TreeView* );
	Void (*__setSensitive_Bool)(struct gtk_TreeView* , Bool);
	Bool (*__isRealized)(struct gtk_TreeView* );
	Void (*__realize)(struct gtk_TreeView* );
	Void (*__forceRepaint_Bool)(struct gtk_TreeView* , Bool);
	Void (*__show)(struct gtk_TreeView* );
	Void (*__showAll)(struct gtk_TreeView* );
	Void (*__hide)(struct gtk_TreeView* );
	Void (*__destroy)(struct gtk_TreeView* );
	Void (*__setPosition_gint_gint)(struct gtk_TreeView* , gint, gint);
	Void (*__setUSize_gint_gint)(struct gtk_TreeView* , gint, gint);
	Void (*__setEvents_Int)(struct gtk_TreeView* , Int);
	GtkAllocation (*__getAllocation)(struct gtk_TreeView* );
	Int (*__getWidth)(struct gtk_TreeView* );
	Int (*__getHeight)(struct gtk_TreeView* );
	struct gtk_Style*  (*__getStyle)(struct gtk_TreeView* );
	Void (*__appendColumn_TreeViewColumn)(struct gtk_TreeView* , struct gtk_TreeViewColumn* );
	
}* gtk_TreeView__class;


typedef struct gtk_TreeView {
	gtk_TreeView__class class;
	GtkTreeView * view;
	struct gtk_TreeModel*  model;
	
}* gtk_TreeView;


extern gtk_TreeView__class gtk_TreeView__classInstance;
GtkObject * __gtk_TreeView_getObject(struct gtk_TreeView* );
Void __gtk_TreeView_connect_String_Func(struct gtk_TreeView* , String, Func);
Void __gtk_TreeView_connect_String_Func_GPointer(struct gtk_TreeView* , String, Func, GPointer);
Void __gtk_TreeView_connectNaked_String_Func(struct gtk_TreeView* , String, Func);
Void __gtk_TreeView_connectNaked_String_Func_GPointer(struct gtk_TreeView* , String, Func, GPointer);
Void __gtk_TreeView_emitByName_String(struct gtk_TreeView* , String);
Void __gtk_TreeView_ref(struct gtk_TreeView* );
Void __gtk_TreeView_unref(struct gtk_TreeView* );
Void __gtk_TreeView_sink(struct gtk_TreeView* );
Void __gtk_TreeView_setProperty_String_GValue__star(struct gtk_TreeView* , String, GValue *);
GtkWidget * __gtk_TreeView_getWidget(struct gtk_TreeView* );
Void __gtk_TreeView_setSensitive_Bool(struct gtk_TreeView* , Bool);
Bool __gtk_TreeView_isRealized(struct gtk_TreeView* );
Void __gtk_TreeView_realize(struct gtk_TreeView* );
Void __gtk_TreeView_forceRepaint_Bool(struct gtk_TreeView* , Bool);
Void __gtk_TreeView_show(struct gtk_TreeView* );
Void __gtk_TreeView_showAll(struct gtk_TreeView* );
Void __gtk_TreeView_hide(struct gtk_TreeView* );
Void __gtk_TreeView_destroy(struct gtk_TreeView* );
Void __gtk_TreeView_setPosition_gint_gint(struct gtk_TreeView* , gint, gint);
Void __gtk_TreeView_setUSize_gint_gint(struct gtk_TreeView* , gint, gint);
Void __gtk_TreeView_setEvents_Int(struct gtk_TreeView* , Int);
GtkAllocation __gtk_TreeView_getAllocation(struct gtk_TreeView* );
Int __gtk_TreeView_getWidth(struct gtk_TreeView* );
Int __gtk_TreeView_getHeight(struct gtk_TreeView* );
struct gtk_Style*  __gtk_TreeView_getStyle(struct gtk_TreeView* );
extern gtk_TreeView __gtk_TreeView_new();
extern gtk_TreeView __gtk_TreeView_new_TreeModel(struct gtk_TreeModel* );
Void __gtk_TreeView_appendColumn_TreeViewColumn(struct gtk_TreeView* , struct gtk_TreeViewColumn* );


#endif // gtk_TreeView_h
