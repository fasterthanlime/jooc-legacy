use gtk;
import Dialog;

class MessageDialog from Dialog {

	//new(Window parent, GtkDialogFlags flags, GtkMessageType type, GtkButtonsType buttons, String text) {
	new(Window parent, Int flags, Int type, Int buttons, String text) {
		dialog = gtk_message_dialog_new(GTK_WINDOW(parent.getObject()), flags, type, buttons, text);
	}
	
	static func newYesNoDialog(Window parent, String text) -> MessageDialog {
		return new MessageDialog(parent, GTK_DIALOG_MODAL, GTK_MESSAGE_QUESTION, GTK_BUTTONS_YES_NO, text);
	}
	
	static func newOkCancelDialog(Window parent, String text) -> MessageDialog {
		return new MessageDialog(parent, GTK_DIALOG_MODAL, GTK_MESSAGE_QUESTION, GTK_BUTTONS_OK_CANCEL, text);
	}

}
