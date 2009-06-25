#ifndef gtk_IconSet_h
#define gtk_IconSet_h

struct gtk_IconSet;

#include <gtk/gtk.h>
#include "../OocLib.h"

typedef struct gtk_IconSet__class {
	String name;
	GtkIconSet * (*__getIconSet)(struct gtk_IconSet* );
	
}* gtk_IconSet__class;


typedef struct gtk_IconSet {
	gtk_IconSet__class class;
	GtkIconSet * iconSet;
	
}* gtk_IconSet;


extern gtk_IconSet__class gtk_IconSet__classInstance;
extern gtk_IconSet __gtk_IconSet_new_GtkIconSet__star(GtkIconSet *);
GtkIconSet * __gtk_IconSet_getIconSet(struct gtk_IconSet* );


#endif // gtk_IconSet_h
