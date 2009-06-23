import SDL;

class WM {

	/**
	 * Sets the title-bar of the display window. 
	 */
	static func setCaption(String title) {
		SDL_WM_SetCaption(title, null);
	}

	/**
	 * Sets the title-bar and icon name of the display window. 
	 */
	static func setCaption(String title, String icon) {
		SDL_WM_SetCaption(title, icon);
	}

}
