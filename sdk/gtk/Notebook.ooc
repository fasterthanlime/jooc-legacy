use gtk;
import Widget, Label;

class Notebook from Widget {

	GtkNotebook* notebook;
	
	implement getObject {
		return GTK_OBJECT(notebook);
	}
	
	func new {
		notebook = gtk_notebook_new();
	}
	
	func appendPage(Widget widget, String title) {
		Label label = new(title);
		gtk_notebook_append_page(notebook, widget.getWidget, label.getWidget);
	}
	
	func getNPages -> Int {
		return gtk_notebook_get_n_pages(notebook);
	}
	
	func setCurrentPage(Int i) {
		gtk_notebook_set_current_page(notebook, i);
	}

}
