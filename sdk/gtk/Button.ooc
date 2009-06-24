use gtk;
import Container;
import Label;
import Image;

/**
 * A widget that creates a signal when clicked on
 */
class Button from Container {

	GtkButton* button;
	
	implement getObject {
		return GTK_OBJECT(button);
	}

	/**
	 * Create a new, empty, button.
	 */
	func new {
		button = GTK_BUTTON(gtk_button_new());
	}
	
	/**
	 * Create a new button from stock.
	 * @param stockId the stock identifier, e.g. "gtk-media-play" or "gtk-ok"
	 */
	func new(String stockId) {
		button = GTK_BUTTON(gtk_button_new_from_stock(stockId));
	}
	
	func setLabel(String text) {
		gtk_button_set_label(button, text);
	}
	
	func setUseStock(gboolean useStock) {
		gtk_button_set_use_stock(button, useStock);
	}
	
	func setImage(Image image) {
		gtk_button_set_image(button, GTK_IMAGE(image.getObject()));
	}
	
	func pressed {
		gtk_button_pressed(button);
	}
	
	static func newTextButton(String text) -> Button {
		
		Button button = new Button();
		button.setLabel(text);
		return button;
		
	}
	
	static func newTextButton(String text, Func callback) -> Button {
		
		Button button = new Button();
		button.setLabel(text);
		button.connect("clicked", callback);
		return button;
		
	}
	
	static func newFromStock(String stockId, Func callback) -> Button {
		
		Button button = new Button(stockId);
		button.connect("clicked", callback);
		return button;
		
	}
	
	static func newFromStock(String stockId, String text, Func callback) -> Button {
		
		Button button = new Button();
		button.setLabel(text);
		button.setImage(new Image(stockId, GTK_ICON_SIZE_LARGE_TOOLBAR));
		button.connect("clicked", callback);
		return button;
		
	}

}
