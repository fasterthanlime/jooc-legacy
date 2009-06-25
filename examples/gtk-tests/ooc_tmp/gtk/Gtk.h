#ifndef gtk_Gtk_h
#define gtk_Gtk_h

struct gtk_Gtk;

#include <gtk/gtk.h>
#include "GObject.h"
#include "../OocLib.h"

typedef struct gtk_Gtk__class {
	String name;
	
}* gtk_Gtk__class;


typedef struct gtk_Gtk {
	gtk_Gtk__class class;
	
}* gtk_Gtk;


extern gtk_Gtk__class gtk_Gtk__classInstance;
extern Void __gtk_Gtk_init_Int__star_String__star__star(Int *, String **);
extern Void __gtk_Gtk_main();
extern Bool __gtk_Gtk_eventsPending();
extern Void __gtk_Gtk_mainIteration();
extern Void __gtk_Gtk_mainQuit();
extern Void __gtk_Gtk_quitAddDestroy_GObject(struct gtk_GObject* );
extern gtk_Gtk __gtk_Gtk_new();


#endif // gtk_Gtk_h
