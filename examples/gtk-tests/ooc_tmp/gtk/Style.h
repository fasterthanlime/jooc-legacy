#ifndef gtk_Style_h
#define gtk_Style_h

struct gtk_Style;

#include <gtk/gtk.h>
#include "IconSet.h"
#include "../OocLib.h"

typedef struct gtk_Style__class {
	String name;
	struct gtk_IconSet*  (*__lookupIconSet_String)(struct gtk_Style* , String);
	
}* gtk_Style__class;


typedef struct gtk_Style {
	gtk_Style__class class;
	GtkStyle * style;
	
}* gtk_Style;


extern gtk_Style__class gtk_Style__classInstance;
extern gtk_Style __gtk_Style_new_GtkStyle__star(GtkStyle *);
struct gtk_IconSet*  __gtk_Style_lookupIconSet_String(struct gtk_Style* , String);


#endif // gtk_Style_h
