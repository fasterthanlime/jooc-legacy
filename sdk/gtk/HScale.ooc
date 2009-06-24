use gtk;
import Widget, Adjustment;

/**
 * 
 */
class HScale from Widget {

	GtkScale* scale;
	Adjustment adjustment;
	
	implement getObject {
		return GTK_OBJECT(scale);
	}

	func new(=adjustment) {
		scale = gtk_hscale_new(GTK_ADJUSTMENT(adjustment.getObject()));
	}

}
