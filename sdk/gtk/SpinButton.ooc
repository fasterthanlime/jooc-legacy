use gtk;
import Widget;

/**
 * Retrieve an integer or floating-point number from the user
 */
class SpinButton from Widget {
	
	GtkSpinButton* spinButton;
	
	implement getObject {
		return GTK_OBJECT(spinButton);
	}

	/**
	 * This is a convenience constructor that allows creation of a numeric
	 * SpinButton without manually creating an adjustment. The value is
	 * initially set to the minimum value and a page increment of 10 * step
	 * is the default. The precision of the spin button is equivalent to
	 * the precision of step.
	 * 
	 * Note that the way in which the precision is derived works best if
	 * step is a power of ten. If the resulting precision is not suitable
	 * for your needs, use gtk_spin_button_set_digits() to correct it.
	 * @param min Minimum allowable value
	 * @param max Maximum allowable value
	 * @param step Increment added or subtracted by spinning the widget
	 * @return The new spin button as a GtkWidget. 
	 */
	func new(Double min, Double max, Double range) {
		spinButton = gtk_spin_button_new_with_range(min, max, range);
	}
	
	/**
	 * Get the value in the spin button.
	 * @return the value of this spin butotn
	 */
	func getValue -> Double {
		return gtk_spin_button_get_value(spinButton);
	}
	
	func setValue(Double value) {
		gtk_spin_button_set_value(spinButton, value);
	}

}
