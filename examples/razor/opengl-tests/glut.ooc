include stdio, stdlib, stdbool;
include time, string;

use glut;

static Bool test;

class Color {
	Double r;
	Double g;
	Double b;
	Double a;

	new(=r, =g, =b, =a);
}

class Window {

	String name;
	Int width;
	Int height;
	Color clearColor;
	Window instance;

	new(=name, =width, =height) {
		instance = this;
	}
	
	func show {
		glutInitDisplayMode(GLUT_DEPTH | GLUT_RGBA);
		glutInitWindowPosition(0, 0);
		glutInitWindowSize(width, height); 
		glutCreateWindow(name);
		glutDisplayFunc(@displayWrapper);
		clearColor = new Color(1.0, 0.5, 0.2, 0.0);
	}

	func updateClearColor {
		clearColor.r = clearColor.r + 0.05;
		if(clearColor.r > 1.0) {
			clearColor.r = 0.0;
		}
		glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
	}

	static func displayWrapper {
		Window.instance.display;
	}

	func display {
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

        new(Int argc, String[] argv) {
                glutInit(&argc, argv);
                window = new Window("My super window", 1024, 768);
        }

        func run {
                window.show();
                glutMainLoop();
        }

}

func main(Int argc, String[] argv) -> Int {

		if(argc >= 2 && strcmp(argv[1], "--test") == 0) {
			test = true;
		}
		
        new Application(argc, argv).run;

}
