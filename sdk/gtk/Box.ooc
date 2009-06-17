use gtk;
import Container;

abstract class Box from Container {
	
	abstract func getBox -> GtkBox*;
	
	implement getObject {
		return GTK_OBJECT(getBox());
	}
	
	func packStart(Widget child, gboolean expand, gboolean fill, gint padding) {
		gtk_box_pack_start(getBox(), child.getWidget(), expand, fill, padding);
	}
	
	func packStart(Widget child) {
		packStart(child, false, false, 0);
	}
	
	func packEnd(Widget child, gboolean expand, gboolean fill, gint padding) {
		gtk_box_pack_end(getBox(), child.getWidget(), expand, fill, padding);
	}
	
	func packEnd(Widget child) {
		packEnd(child, false, false, 0);
	}

}
