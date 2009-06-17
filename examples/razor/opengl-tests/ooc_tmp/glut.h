#ifndef glut_h
#define glut_h


struct Color;

struct Window;

struct Application;

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <string.h>
#include <GL/gl.h>
#include <GL/glu.h>
#include <GL/glut.h>
#include "OocLib.h"


typedef struct Color__class {
	String name;
	
}* Color__class;



typedef struct Color {
	Color__class class;
	Double r;
	Double g;
	Double b;
	Double a;
	
}* Color;


extern Color__class Color__classInstance;
extern Color __Color_new_Double_Double_Double_Double(Double, Double, Double, Double);


typedef struct Window__class {
	String name;
	Void (*__show)(struct Window* );
	Void (*__updateClearColor)(struct Window* );
	Void (*__display)(struct Window* );
	
}* Window__class;



typedef struct Window {
	Window__class class;
	String name;
	Int width;
	Int height;
	struct Color*  clearColor;
	struct Window*  instance;
	
}* Window;


extern Window__class Window__classInstance;
extern Window __Window_new_String_Int_Int(String, Int, Int);
Void __Window_show(struct Window* );
Void __Window_updateClearColor(struct Window* );
extern Void __Window_displayWrapper();
Void __Window_display(struct Window* );


typedef struct Application__class {
	String name;
	Void (*__run)(struct Application* );
	
}* Application__class;



typedef struct Application {
	Application__class class;
	struct Window*  window;
	
}* Application;


extern Application__class Application__classInstance;
extern Application __Application_new_Int_String__array(Int, String[]);
Void __Application_run(struct Application* );
Int main(Int, String[]);


#endif // glut_h
