use gtk;
import Button;

/**
 * Create buttons which retain their state
 */
class ToggleButton from Button {

	/**
	 * Sets the status of the toggle button. Set to true if you want
	 * the ToggleButton to be 'pressed in', and false to raise it.
	 * This action causes the toggled signal to be emitted. 
	 */
	func setActive(Bool active) {
		gtk_toggle_button_set_active(GTK_TOGGLE_BUTTON(this.getObject()), active);
	}
	
	/**
	 * same as setActive(), but doesn't send the toggled signal.
	 */
	func setActiveQuietly(Bool active) {
		GValue value = {0, };
		g_value_init(&value, G_TYPE_BOOLEAN);
		g_value_set_boolean(&value, active);
		this.setProperty("active", &value);
	}

}
