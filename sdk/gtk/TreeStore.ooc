use gtk;
import TreeModel;

/**
 * A tree-like data structure that can be used with the TreeView
 */
class TreeStore from TreeModel {
	
	GtkTreeStore* store;
	
	implement getModel {
		return GTK_TREE_MODEL(store);
	}
	
	/**
	 * Create a new TreeStore with one column of type G_TYPE_STRING
	 */
	new() {
		store = gtk_tree_store_new(1, G_TYPE_STRING);
	}
	
	/**
	 * Create a new TreeStore with 'numColumns' columns of type
	 * 'types'
	 */
	new(int numColumns, Int[] types) {
		store = gtk_tree_store_newv(numColumns, types);
	}
	
}
