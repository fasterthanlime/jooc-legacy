#ifndef glut_H
#define glut_H
typedef char *String;

typedef struct Color {
	// Personal
	double r;
	double g;
	double b;
	double a;
}* Color;

Color Color_new_double_double_double_double(double, double, double, double);

typedef struct Window {
	// Personal
	String name;
	int width;
	int height;
	Color clearColor;
	void (*show)(struct Window*);
	void (*updateClearColor)(struct Window*);
	void (*display)();
}* Window;

Window Window_new_String_int_int(String, int, int);
void Window_show(struct Window*);
void Window_updateClearColor(struct Window*);
void Window_display(struct Window*);

typedef struct Application {
	// Personal
	Window window;
	void (*run)(struct Application*);
}* Application;

Application Application_new_int__star_char__star__star(int *, char **);
void Application_run(struct Application*);

#endif // glut_H
