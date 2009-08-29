use gtk;
import Container;

class Frame from Container {

	GtkFrame* frame;
	
	implement getObject {
		return GTK_OBJECT(frame);
	}

	func new(String label) {
		frame = gtk_frame_new(label);
	}
	
	func setLabel(String label) {
		gtk_frame_set_label(frame, label);
	}

}
