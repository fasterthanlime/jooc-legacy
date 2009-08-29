use gtk;
import GObject;

/**
 * Singleton GTK class for initialization etc.
 */
class Gtk {

	/**
	 * Initialize Gtk, usually called from main, with Gtk.init(&argc, &argv)
	 * @param argc a pointer to the number of arguments passed to the program
	 * @param argv a pointer to the array of arguments as strings passed to the program
	 */
	static func init(Int* argc, String** argv) {
		gtk_init(argc, argv);
	}

	/**
	 * Start the Gtk main loop
	 */
	static func main {
		gtk_main();
	}
	
	/**
	 * @return true if the event queue is not empty
	 */
	static func eventsPending -> Bool {
		return gtk_events_pending();
	}
	
	/**
	 * Iterate the gtk main loop
	 */
	static func mainIteration {
		gtk_main_iteration();
	}
	
	/**
	 * Quit the Gtk main loop
	 */
	static func mainQuit {
		gtk_main_quit();
	}
	
	/**
	 * Add an object to the list of objects to be destroyed at the end
	 * of the application
	 * @param object
	 */
	static func quitAddDestroy(GObject object) {
		gtk_quit_add_destroy(1, GTK_OBJECT(object.getObject()));
	}

}
