use gtk;
import IconSet;

class IconFactory {

	GtkIconFactory* factory;
	
	new() {
		factory = gtk_icon_factory_new();
	}

	func addDefault() {
		gtk_icon_factory_add_default(factory);
	}
	
	func add(String stockId, IconSet iconSet) {
		gtk_icon_factory_add(factory, stockId, iconSet.getIconSet());
	}

}
