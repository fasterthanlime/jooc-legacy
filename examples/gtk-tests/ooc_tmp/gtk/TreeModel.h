#ifndef gtk_TreeModel_h
#define gtk_TreeModel_h

struct gtk_TreeModel;

#include <gtk/gtk.h>
#include "../OocLib.h"

typedef struct gtk_TreeModel__class {
	String name;
	GtkTreeModel * (*__getModel)(struct gtk_TreeModel* );
	
}* gtk_TreeModel__class;


typedef struct gtk_TreeModel {
	gtk_TreeModel__class class;
	
}* gtk_TreeModel;


extern gtk_TreeModel__class gtk_TreeModel__classInstance;
GtkTreeModel * __gtk_TreeModel_getModel(struct gtk_TreeModel* );


#endif // gtk_TreeModel_h
