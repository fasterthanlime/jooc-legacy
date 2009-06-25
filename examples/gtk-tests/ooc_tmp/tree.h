#ifndef tree_h
#define tree_h


#include "gtk/Gtk.h"
#include "gtk/TreeView.h"
#include "gtk/TreeStore.h"
#include "OocLib.h"
#include "gtk/Window.h"
#include "gtk/TreeViewColumn.h"
#include "gtk/TreeNode.h"
typedef enum Column {
	COL_TITLE,
	COL_DESC,
	NUM_COLS,
} Column;
Int main(Int, String[]);


#endif // tree_h
