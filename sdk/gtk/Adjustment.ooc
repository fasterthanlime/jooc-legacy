use gtk;
import GObject;

class Adjustment from GObject {

	GtkAdjustment* adjustment;

	implement getObject {
		return GTK_OBJECT(adjustment);
	}

	new(Double lower, Double upper, Double step) {
		adjustment = gtk_adjustment_new(lower, lower, upper, step, step, 1);
	}
	
	func getLower -> Double {
		return gtk_adjustment_get_lower(adjustment);
	}
	
	func setLower(Double value) {
		return gtk_adjustment_set_lower(adjustment, value);
	}
	
	func getUpper -> Double {
		return gtk_adjustment_get_upper(adjustment);
	}
	
	func setUpper(Double value) {
		return gtk_adjustment_set_upper(adjustment, value);
	}
	
	func getValue -> Double {
		return gtk_adjustment_get_value(adjustment);
	}
	
	func setValue(Double value) {
		gtk_adjustment_set_value(adjustment, value);
	}

}
