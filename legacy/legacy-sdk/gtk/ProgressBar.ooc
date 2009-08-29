use gtk;
import Widget;

/**
 * A widget which indicates progress visually
 */
class ProgressBar from Widget {

	GtkProgressBar* progressBar;
	
	implement getObject {
		return GTK_OBJECT(progressBar);
	}
	
	/**
	 * Creates a new progress bar
	 */
	func new {
		progressBar = gtk_progress_bar_new();
	}
	
	/**
	 * Causes the progress bar to "fill in" the given fraction of the
	 * bar. The fraction should be between 0.0 and 1.0, inclusive.
	 * @param fraction of the task that's been completed 
	 */
	func setFraction(gdouble fraction) {
		gtk_progress_bar_set_fraction(progressBar, fraction);
	}
	
	/**
	 * Causes the given text to appear superimposed on the progress bar.
	 * @param text a UTF-8 string, or null
	 */
	func setText(String text) {
		gtk_progress_bar_set_text(progressBar, text);
	}

}
