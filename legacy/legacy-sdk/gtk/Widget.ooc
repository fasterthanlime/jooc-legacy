use gtk;
import Gtk;
import GObject;
import Style;
include stdio;

/**
 * A GTK widget, such as a Button, a Label, a Checkbox
 */
abstract class Widget from GObject {

	func getWidget -> GtkWidget* {
		return GTK_WIDGET(this.getObject());
	}
		
	/**
	 * Set the sensitivity of this widget
	 * @param sensitive if true, the widget will react to the user
	 * input, and send/receive signals as usual. If false, thewidget
	 * will be "grayed out" and won't react to anything
	 */
	func setSensitive(Bool sensitive) {
		gtk_widget_set_sensitive(getWidget(), sensitive);
	}

	func isRealized -> Bool {
		return GTK_WIDGET_REALIZED(getWidget());
	}
	
	/**
	 * Realize this component on-screen, e.g. allocate resources, etc.
	 * It's often not needed to call it directly, use show() instead.
	 */
	func realize {
		gtk_widget_realize(getWidget());
	}
	
	/**
	 * Force the repaint of this widget
	 */
	func forceRepaint(Bool childrenToo) {		
		
		while(Gtk.eventsPending()) {
			Gtk.mainIteration();
		}
		gdk_window_invalidate_rect(getWidget()->window, null, childrenToo);
		gdk_window_process_updates(getWidget()->window, childrenToo);
		
	}
	
	/**
	 * Shows this widget on-screen.
	 */
	func show {
		gtk_widget_show(getWidget());
	}
	
	/**
	 * Shows this widget on-screen and all its children.
	 */
	func showAll {
		gtk_widget_show_all(getWidget());
	}
	
	/**
	 * Hides this widget
	 */
	func hide {
		gtk_widget_hide(getWidget());
	}
	
	func destroy {
		gtk_widget_destroy(getWidget());
	}
	
	/**
	 * set the position of this wdiget
	 * @param x the x coordinate of the desired position for this widget, or
	 * -1 for default position
	 * @param y the y coordinate of the desired position for this widget, or
	 * -1 for default position
	 */
	func setUPosition(gint x, gint y) {
		gtk_widget_set_uposition(getWidget(), x, y);
	}
	
	/**
	 * set the size of this widget
	 * @param width the desired width for this widget, or -1 for the default
	 * @param height the desired height for this widget, or -1 for the default
	 */
	func setUSize(gint width, gint height) {
		gtk_widget_set_usize(getWidget(), width, height);
	}
	
	/**
	 * Adjust the event mask of this widget
	 */
	func setEvents(Int eventMask) {
		gtk_widget_set_events(getWidget(), eventMask);
	}
	
	/**
	 * Return the size of the window, as a GtkAllocation (a GdkRectangle, really)
	 */
	func getAllocation -> GtkAllocation {
		return getWidget()->allocation;
	}
	
	/**
	 * The height of this window
	 */
	func getWidth -> Int {
		return getWidget()->allocation.width;
	}
	
	/**
	 * The height of this window
	 */
	func getHeight -> Int {
		return getWidget()->allocation.height;
	}
	
	func getStyle -> Style {
		return new Style(gtk_widget_get_style(getWidget()));
	}

}
