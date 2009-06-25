#ifndef gtk_TreeStore_h
#define gtk_TreeStore_h

struct gtk_TreeStore;

#include <gtk/gtk.h>
#include "TreeModel.h"
#include "../OocLib.h"

typedef struct gtk_TreeStore__class {
	String name;
	GtkTreeModel * (*__getModel)(struct gtk_TreeStore* );
	
}* gtk_TreeStore__class;


typedef struct gtk_TreeStore {
	gtk_TreeStore__class class;
	GtkTreeStore * store;
	
}* gtk_TreeStore;


extern gtk_TreeStore__class gtk_TreeStore__classInstance;
GtkTreeModel * __gtk_TreeStore_getModel(struct gtk_TreeStore* );
extern gtk_TreeStore __gtk_TreeStore_new();
extern gtk_TreeStore __gtk_TreeStore_new_int_Int__array(int, Int[]);


#endif // gtk_TreeStore_h
