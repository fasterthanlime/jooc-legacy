use gtkglarea;
include stdio, stdlib, string;

import GObject;

class GdkGl {
	
	func new;

	static func query -> Bool {
		
		return gdk_gl_query();
		
	}
	
	static func getInfo -> String {
		
		// make the String garbage-collected
		GChar* ginfo = gdk_gl_get_info();
		Int length = strlen(ginfo) + 1; // For the final '\0'
		String info = malloc(length);
		strncpy(info, ginfo, length);
		g_free(ginfo);
		return info;
		
	}

}
