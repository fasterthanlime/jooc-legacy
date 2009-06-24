use gtk;
import Widget;

ctype GtkIconSize;

class Image from Widget {

	GtkImage* image;
	
	implement getObject {
		return GTK_OBJECT(image);
	}
	
	/**
	 * Create a new image from stock
	 * @param stockId the ID of the image, e.g. "gtk-media-play", etc
	 * @param the size of the image
	 */
	func new(String stockId, GtkIconSize size) {
	//func new(String stockId, Int size) {
		gtk_image_new_from_stock(stockId, size);
	}

}
