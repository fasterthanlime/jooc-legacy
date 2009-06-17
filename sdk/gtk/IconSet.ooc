use gtk;

class IconSet {

	GtkIconSet* iconSet;
	
	new(=iconSet);
	
	func getIconSet -> GtkIconSet* {
		iconSet;
	}

}
