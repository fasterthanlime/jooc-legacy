import structs.Array;
import lang.String;

include stdio, stdlib, stdbool;
include time, string;

use glut;

static Bool test;

class Color {
	
	Double r, g, b, a;
	
	func new(=r, =g, =b, =a);
	
}

class Window {

	static Window instance;
	String name;
	Int width, height;
	Color clearColor;

	func new(=name, =width, =height) {
		instance = this;
	}
	
	func show {
		glutInitDisplayMode(GLUT_DEPTH | GLUT_RGBA);
		glutInitWindowPosition(0, 0);
		glutInitWindowSize(width, height); 
		glutCreateWindow(name);
		glutDisplayFunc(@display);
		clearColor = new Color(1.0, 0.5, 0.2, 0.0);
	}

	func updateClearColor {
		clearColor.r = clearColor.r + 0.05;
		if(clearColor.r > 1.0) {
			clearColor.r = 0.0;
		}
		glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
	}

	func display {
		this = Window.instance;
		updateClearColor;
		glClear(GL_COLOR_BUFFER_BIT);
		glutSwapBuffers();
		if(test) {
			printf("In test mode, exiting gracefully =)\n");
			exit(0);
		}
	}

}

class Application {

	Window window;

	func new(Int argc, String[] argv) {
		glutInit(&argc, argv);
		window = new Window("My super window", 1024, 768);
	}

	func run {
		window.show();
		glutMainLoop();
	}

}

func main(Int argc, String[] argv) -> Int {

	if(argc >= 2 && argv[1].equals("--test")) {
		test = true;
	}
	
	new Application(argc, argv).run;

}
