use gtk;
import Container;

/**
 * A widget used to catch events for widgets which do not have their
 * own window
 */
class EventBox from Container {
	
	GtkEventBox* eventBox;
	
	implement getObject {
		return GTK_OBJECT(eventBox);
	}
	
	new() {
		eventBox = gtk_event_box_new();
	}
	
}
