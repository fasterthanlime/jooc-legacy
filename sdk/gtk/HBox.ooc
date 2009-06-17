use gtk;
import Box;

class HBox from Box {

	GtkHBox* box;

	implement getBox {
		return GTK_BOX(box);
	}

	new(gboolean homogeneous, gint spacing) {
		box = gtk_hbox_new(homogeneous, spacing);
	}

}
