use gtk;
import Box;

class VBox from Box {

	GtkVBox* box;

	implement getBox {
		return GTK_BOX(box);
	}

	func new(gboolean homogeneous, gint spacing) {
		box = gtk_vbox_new(homogeneous, spacing);
	}

}
