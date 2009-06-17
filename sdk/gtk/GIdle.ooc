use gtk;

/**
 * Functions related to idle functions called when the glib mainloop
 * has nothing else to do.
 */
class GIdle {

	static guint add(Func Func) {
		return GIdle.add(Func, null);
	}
	
	static guint add(Func Func, gpointer data) {
		return g_idle_add(Func, data);
	}

}
