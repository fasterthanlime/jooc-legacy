use gtk;
import Container;

/**
 * A simple Gtk window
 */
class Window from Container {

	GtkWindow* window;
	
	implement getObject {
		return GTK_OBJECT(window);
	}

	/**
	 * Create a new top-level window
	 */
	func new {
		window = GTK_WINDOW(gtk_window_new(GTK_WINDOW_TOPLEVEL));
	}
	
	/**
	 * Create a new titled top-level window
	 */
	func new(String title) {
		this();
		setTitle(title);
	}
	
	/**
	 * Change the title of this window
	 */
	func setTitle(String title) {
		gtk_window_set_title(GTK_WINDOW(window), title);
	}

}
