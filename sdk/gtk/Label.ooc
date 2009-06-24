use gtk;
import Widget;

/**
 * A Label displays text in a Container.
 * It can be added e.g. to a button
 */
class Label from Widget {

	GtkLabel* label;
	
	implement getObject {
		return GTK_OBJECT(label);
	}
	
	/**
	 * Create a new empty label
	 */
	func new {
		label = gtk_label_new(null);
	}
	
	/**
	 * Create a new label with specified text
	 * @param text The text to be displayed on the label
	 */
	func new(String text) {
		label = gtk_label_new(text);
	}
	
	/**
	 * Sets the text of this label as Pango markup, e.g.
	 * with <small></small> for small text and <b></b> for bold.
	 * @param markup valid Pango markup
	 * {@link http://library.gnome.org/devel/pango/stable/PangoMarkupFormat.html}
	 */
	func setMarkup(String markup) {
		gtk_label_set_markup(label, markup);
	}

}
