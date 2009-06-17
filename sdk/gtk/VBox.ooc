use gtk;
import Box;

class VBox from Box {

	GtkVBox* box;

	implement getBox {
		return GTK_BOX(box);
	}

	new(gboolean homogeneous, gint spacing) {
		box = gtk_vbox_new(homogeneous, spacing);
	}

}
