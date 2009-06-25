#ifndef gtk_TreeNode_h
#define gtk_TreeNode_h

struct gtk_TreeNode;

#include <gtk/gtk.h>
#include "TreeStore.h"
#include "../OocLib.h"

typedef struct gtk_TreeNode__class {
	String name;
	Void (*__setValue_Object)(struct gtk_TreeNode* , Object);
	Void (*__setValue_int_Object)(struct gtk_TreeNode* , int, Object);
	
}* gtk_TreeNode__class;


typedef struct gtk_TreeNode {
	gtk_TreeNode__class class;
	GtkTreeIter iter;
	struct gtk_TreeStore*  store;
	
}* gtk_TreeNode;


extern gtk_TreeNode__class gtk_TreeNode__classInstance;
extern gtk_TreeNode __gtk_TreeNode_new_TreeStore(struct gtk_TreeStore* );
extern gtk_TreeNode __gtk_TreeNode_new_TreeStore_gtk_TreeNode(struct gtk_TreeStore* , struct gtk_TreeNode* );
Void __gtk_TreeNode_setValue_Object(struct gtk_TreeNode* , Object);
Void __gtk_TreeNode_setValue_int_Object(struct gtk_TreeNode* , int, Object);


#endif // gtk_TreeNode_h
