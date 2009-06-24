use gtk;
import TreeStore;

class TreeNode {

	GtkTreeIter iter;
	TreeStore store;

	func new(=store) {
		gtk_tree_store_append(this.store.store, &iter, null);
	}
	
	func new(=store, This parent) {
		gtk_tree_store_append(this.store.store, &iter, &(parent.iter));
	}
	
	/**
	 * Set the value of the first column
	 */
	func setValue(Object value) {
		gtk_tree_store_set(store.store, &iter, 0, value, -1);
	}
	
	func setValue(int column, Object value) {
		gtk_tree_store_set(store.store, &iter, column, value, -1);
	}

}
