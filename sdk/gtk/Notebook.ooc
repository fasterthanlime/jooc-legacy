use gtk;
import Widget, Label;

class Notebook from Widget {

	GtkNotebook* notebook;
	
	implement getObject {
		return GTK_OBJECT(notebook);
	}
	
	new() {
		notebook = gtk_notebook_new();
	}
	
	func appendPage(Widget widget, String title) {
		Label label = new(title);
		gtk_notebook_append_page(notebook, widget.getWidget, label.getWidget);
	}

}
