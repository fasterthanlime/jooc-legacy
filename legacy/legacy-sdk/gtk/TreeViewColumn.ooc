include stdio;
use gtk;
import GObject;

class TreeViewColumn from GObject {

	GtkTreeViewColumn* column;
	
	implement getObject {
		return GTK_OBJECT(column);
	}
	
	/**
	 * Create a new TreeViewColumn to the first colun
	 */
	func new(String titleColumn) {
		this(titleColumn, 0);
	}
	
	func new(String title, Int columnId) {
		GtkCellRenderer* renderer = gtk_cell_renderer_text_new();
		column = gtk_tree_view_column_new_with_attributes(title, renderer, null);
		gtk_tree_view_column_add_attribute(column, renderer, "text", columnId);
		gtk_tree_view_column_pack_start(column, renderer, true);
	}

}
