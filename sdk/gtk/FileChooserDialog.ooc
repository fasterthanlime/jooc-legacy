use gtk;

import Dialog;
import Window;

class FileChooserDialog from Dialog {

	func new(String title, Window parent, Int action) {
		
		Int stockId;
		if(action == GTK_FILE_CHOOSER_ACTION_SAVE) {
			stockId = GTK_STOCK_SAVE;
		} else {
			stockId = GTK_STOCK_OPEN;
		}
		
		dialog = gtk_file_chooser_dialog_new(title,
					GTK_WINDOW(parent.getObject()),
					action,
				    GTK_STOCK_CANCEL, GTK_RESPONSE_CANCEL,
				    stockId, GTK_RESPONSE_ACCEPT,
				    null);
					
	}
	
	func getFileName -> String {
		
		GChar* gfileName = gtk_file_chooser_get_filename(dialog);
		Int length = strlen(gfileName) + 1; // For the final '\0'
		String fileName = malloc(length);
		strncpy(fileName, gfileName, length);
		g_free(gfileName);
		return fileName;
		
	}

}
