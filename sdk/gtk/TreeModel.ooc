use gtk;

/**
 * The tree interface used by GtkTreeView
 */
abstract class TreeModel {

	abstract func getModel -> GtkTreeModel*;

}
