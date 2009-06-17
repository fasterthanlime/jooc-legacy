use gtk;
import Widget;
import Adjustment;

/**
 * 
 */
class HScrollbar from Widget {

	GtkScrollbar* scrollbar;
	Adjustment adjustment;
	
	implement getObject {
		return GTK_OBJECT(scrollbar);
	}

	new(=adjustment) {
		scrollbar = gtk_hscrollbar_new(GTK_ADJUSTMENT(adjustment.getObject()));
	}

}
