use gtk;

/**
 * Functions related to idle functions called when the glib mainloop
 * has nothing else to do.
 */
class GTimeout {

	/**
	 * Add a Func to be called each 'interval' milliseconds
	 */
	static func add(guint32 interval, Func Func) -> guint {
		return GTimeout.add(interval, Func, null);
	}
	
	static func add(guint32 interval, Func Func, gpointer data) -> guint {
		return g_timeout_add(interval, Func, data);
	}

}
