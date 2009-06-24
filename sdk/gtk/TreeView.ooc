use gtk;
import Widget;
import TreeModel;
import TreeViewColumn;

/**
 * A widget for displaying both trees and lists
 */
class TreeView from Widget {

	GtkTreeView* view;
	TreeModel model;
	
	implement getObject {
		return GTK_OBJECT(view);
	}
	
	func new {
		view = gtk_tree_view_new();
	}
	
	func new(TreeModel model) {
		view = gtk_tree_view_new_with_model(model.getModel);
		this.model = model;
	}
	
	func appendColumn(TreeViewColumn column) {
		gtk_tree_view_append_column(GTK_TREE_VIEW(view), GTK_TREE_VIEW_COLUMN(column.getObject));
	}

}
