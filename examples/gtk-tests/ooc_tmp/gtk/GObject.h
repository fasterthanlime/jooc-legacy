#ifndef gtk_GObject_h
#define gtk_GObject_h

struct gtk_SignalPacket;
struct gtk_GObject;

#include <gtk/gtk.h>
#include "../OocLib.h"
typedef gchar GChar;
typedef gpointer GPointer;
typedef gulong GULong;

typedef struct gtk_SignalPacket__class {
	String name;
	Void (*__call)(struct gtk_SignalPacket* );
	
}* gtk_SignalPacket__class;


typedef struct gtk_SignalPacket {
	gtk_SignalPacket__class class;
	struct gtk_GObject*  object;
	GPointer data;
	Func callback;
	
}* gtk_SignalPacket;


extern gtk_SignalPacket__class gtk_SignalPacket__classInstance;
extern gtk_SignalPacket __gtk_SignalPacket_new_GObject_GPointer_Func(struct gtk_GObject* , GPointer, Func);
Void __gtk_SignalPacket_call(struct gtk_SignalPacket* );
Void __signalWrapper_GtkObject__star_GPointer(GtkObject *, GPointer);

typedef struct gtk_GObject__class {
	String name;
	GtkObject * (*__getObject)(struct gtk_GObject* );
	Void (*__connect_String_Func)(struct gtk_GObject* , String, Func);
	Void (*__connect_String_Func_GPointer)(struct gtk_GObject* , String, Func, GPointer);
	Void (*__connectNaked_String_Func)(struct gtk_GObject* , String, Func);
	Void (*__connectNaked_String_Func_GPointer)(struct gtk_GObject* , String, Func, GPointer);
	Void (*__emitByName_String)(struct gtk_GObject* , String);
	Void (*__ref)(struct gtk_GObject* );
	Void (*__unref)(struct gtk_GObject* );
	Void (*__sink)(struct gtk_GObject* );
	Void (*__setProperty_String_GValue__star)(struct gtk_GObject* , String, GValue *);
	
}* gtk_GObject__class;


typedef struct gtk_GObject {
	gtk_GObject__class class;
	
}* gtk_GObject;


extern gtk_GObject__class gtk_GObject__classInstance;
GtkObject * __gtk_GObject_getObject(struct gtk_GObject* );
Void __gtk_GObject_connect_String_Func(struct gtk_GObject* , String, Func);
Void __gtk_GObject_connect_String_Func_GPointer(struct gtk_GObject* , String, Func, GPointer);
Void __gtk_GObject_connectNaked_String_Func(struct gtk_GObject* , String, Func);
Void __gtk_GObject_connectNaked_String_Func_GPointer(struct gtk_GObject* , String, Func, GPointer);
Void __gtk_GObject_emitByName_String(struct gtk_GObject* , String);
Void __gtk_GObject_ref(struct gtk_GObject* );
Void __gtk_GObject_unref(struct gtk_GObject* );
Void __gtk_GObject_sink(struct gtk_GObject* );
Void __gtk_GObject_setProperty_String_GValue__star(struct gtk_GObject* , String, GValue *);


#endif // gtk_GObject_h
