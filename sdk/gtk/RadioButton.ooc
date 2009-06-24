use gtk;
import ToggleButton;

class RadioButton from ToggleButton {

	/**
	 * Creates a new RadioButton. To be of any practical value,
	 * a widget should then be packed into the radio button. 
	 */
	func new {
		button = gtk_radio_button_new(null);
	}
	
	/**
	 * Creates a new RadioButton, adding it to the same group as
	 * groupMember. As with new(), a widget should be packed into
	 * the radio button.
	 * @param groupMember an existing RadioButton
	 */
	func new(RadioButton groupMember) {
		button = gtk_radio_button_new_from_widget(GTK_RADIO_BUTTON(groupMember.button));
	}
	
	/**
	 * Creates a new RadioButton with a text label. 
	 */
	func new(String label) {
		button = gtk_radio_button_new_with_label(null, label);
	}
	
	/**
	 * Creates a new GtkRadioButton with a text label,
	 * adding it to the same group as groupMember. 
	 */
	func new(RadioButton groupMember, String label) {
		button = gtk_radio_button_new_with_label_from_widget(GTK_RADIO_BUTTON(groupMember.button), label);
	}

}
