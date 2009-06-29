use glu;

import lang.String;

import gtk.Gtk;
import gtk.GlArea;
import gtk.Window;
import gtk.Button;
import gtk.VBox;

static Bool test;

func main(Int argc, String[] argv) {

	Gtk.init(&argc, &argv);

	test = (argc >= 2 && argv[1].equals("--test"));

	new App;

}

class App {

	static This instance;
	GlArea area;
	Window w;

	func new {
		instance = this;

		w = new("GtkGlArea ftw!");
		
		Int[] flags = {
			GDK_GL_RGBA,
			GDK_GL_RED_SIZE, 1,
			GDK_GL_GREEN_SIZE, 1,
			GDK_GL_BLUE_SIZE, 1,
			GDK_GL_DOUBLEBUFFER,
			GDK_GL_NONE	
		};
		area = new(flags);
		area.setUSize(400, 400);
		area.connectNaked("realize", @setupView);
		area.connectNaked("configure_event", @resizeView);
		area.connectNaked("expose_event", @drawView);

		VBox b = new(false, 0);
		w.add(b);

		b.packStart(area, true, true, 0);
		b.packEnd(Button.newFromStock("gtk-quit", Gtk.@mainQuit));

		w.showAll;
		w.connectNaked("destroy", Gtk.@mainQuit);

		Gtk.main;

	}

	func setupView {

		this = This.instance; // We're called from a callback, so 'this' is garbage.

		if(!area.makeCurrent) return; // The area should be made current before making gl calls

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		gluOrtho2D(0.0, 1.0, 1.0, 0.0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

	}

	func resizeView {

		this = This.instance;

		if(!area.makeCurrent) return;

		glViewport(0, 0, area.getWidth, area.getHeight);

	}

	func drawView {

		this = This.instance;

		if(!area.makeCurrent) return;

		glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // rgba
		glClear(GL_COLOR_BUFFER_BIT);

		glBegin(GL_QUADS);

		glVertex2f(0.0f, 0.0f);
		glColor3f(1.0f, 0.0f, 0.0f);

		glVertex2f(1.0f, 0.0f);
		glColor3f(0.0f, 1.0f, 0.0f);

		glVertex2f(1.0f, 1.0f);
		glColor3f(0.0f, 0.0f, 1.0f);

		glVertex2f(0.0f, 1.0f);
		glColor3f(1.0f, 1.0f, 0.0f);

		glEnd();

		glFlush();
		
		area.swapBuffers;

		if(test) {
			w.destroy;
		}

	}

}
