use gtkglarea;
import Widget;

class GlArea from Widget {

	GtkGLArea* glArea;
	
	implement getObject {
		return GTK_OBJECT(glArea);
	}

	new(Int[] attributes) {
		glArea = GTK_GL_AREA(gtk_gl_area_new(attributes));
	}
	
	func makeCurrent -> gint {
		return gtk_gl_area_make_current(glArea);
	}
	
	func swapBuffers {
		gtk_gl_area_swapbuffers(glArea);
	}

}
