use gtk;

class IconSet {

	GtkIconSet* iconSet;
	
	func new(=iconSet);
	
	func getIconSet -> GtkIconSet* {
		iconSet;
	}

}
