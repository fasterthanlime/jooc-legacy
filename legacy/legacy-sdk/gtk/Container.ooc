use gtk;
import Widget;

/**
 * A container is a widget that can contain other widgets
 */
abstract class Container from Widget {

	/**
	 * Add a widget to this container
	 * @param widget the widget to add
	 */
	func add(Widget widget) {
		gtk_container_add(GTK_CONTAINER(this.getWidget()), widget.getWidget());
	}
	
	/**
	 * Remove a widget from this container
	 * @param widget the widget to remove
	 */
	func remove(Widget widget) {
		gtk_container_remove(GTK_CONTAINER(this.getWidget()), widget.getWidget());
	}
	
	/**
	 * Change the width of the border of this container
	 * @param width
	 */
	func setBorderWidth(Int width) {
		gtk_container_set_border_width(GTK_CONTAINER(this.getWidget()), width);
	}

}

