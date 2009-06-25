#ifndef gtk_TreeViewColumn_h
#define gtk_TreeViewColumn_h

struct gtk_TreeViewColumn;

#include <stdio.h>
#include <gtk/gtk.h>
#include "GObject.h"
#include "../OocLib.h"

typedef struct gtk_TreeViewColumn__class {
	String name;
	GtkObject * (*__getObject)(struct gtk_TreeViewColumn* );
	Void (*__connect_String_Func)(struct gtk_TreeViewColumn* , String, Func);
	Void (*__connect_String_Func_GPointer)(struct gtk_TreeViewColumn* , String, Func, GPointer);
	Void (*__connectNaked_String_Func)(struct gtk_TreeViewColumn* , String, Func);
	Void (*__connectNaked_String_Func_GPointer)(struct gtk_TreeViewColumn* , String, Func, GPointer);
	Void (*__emitByName_String)(struct gtk_TreeViewColumn* , String);
	Void (*__ref)(struct gtk_TreeViewColumn* );
	Void (*__unref)(struct gtk_TreeViewColumn* );
	Void (*__sink)(struct gtk_TreeViewColumn* );
	Void (*__setProperty_String_GValue__star)(struct gtk_TreeViewColumn* , String, GValue *);
	
}* gtk_TreeViewColumn__class;


typedef struct gtk_TreeViewColumn {
	gtk_TreeViewColumn__class class;
	GtkTreeViewColumn * column;
	
}* gtk_TreeViewColumn;


extern gtk_TreeViewColumn__class gtk_TreeViewColumn__classInstance;
GtkObject * __gtk_TreeViewColumn_getObject(struct gtk_TreeViewColumn* );
Void __gtk_TreeViewColumn_connect_String_Func(struct gtk_TreeViewColumn* , String, Func);
Void __gtk_TreeViewColumn_connect_String_Func_GPointer(struct gtk_TreeViewColumn* , String, Func, GPointer);
Void __gtk_TreeViewColumn_connectNaked_String_Func(struct gtk_TreeViewColumn* , String, Func);
Void __gtk_TreeViewColumn_connectNaked_String_Func_GPointer(struct gtk_TreeViewColumn* , String, Func, GPointer);
Void __gtk_TreeViewColumn_emitByName_String(struct gtk_TreeViewColumn* , String);
Void __gtk_TreeViewColumn_ref(struct gtk_TreeViewColumn* );
Void __gtk_TreeViewColumn_unref(struct gtk_TreeViewColumn* );
Void __gtk_TreeViewColumn_sink(struct gtk_TreeViewColumn* );
Void __gtk_TreeViewColumn_setProperty_String_GValue__star(struct gtk_TreeViewColumn* , String, GValue *);
extern gtk_TreeViewColumn __gtk_TreeViewColumn_new_String(String);
extern gtk_TreeViewColumn __gtk_TreeViewColumn_new_String_Int(String, Int);


#endif // gtk_TreeViewColumn_h
