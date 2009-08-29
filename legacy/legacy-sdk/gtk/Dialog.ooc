use gtk;
import Window;

class Dialog from Window {

	GtkDialog* dialog;
	
	override getObject {
		return GTK_OBJECT(dialog);
	}
	
	func run -> gint {
		return gtk_dialog_run(dialog);
	}
	
	func add(Widget widget) {
		GtkContainer* contentArea = GTK_CONTAINER(gtk_dialog_get_content_area(dialog));
		gtk_container_add(contentArea, widget.getWidget());
		this.showAll();
	}

}
