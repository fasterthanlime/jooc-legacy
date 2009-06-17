use gtk;
import IconSet;

class Style {

	GtkStyle* style

	new(=style);
	
	func lookupIconSet(String stockId) -> IconSet {
		
		GtkIconSet* iconSet = gtk_style_lookup_icon_set(style, stockId);
		if(iconSet) {
			return new IconSet(iconSet);
		} else {
			return null;
		}
		
	}
	
}
