include SDL/SDL_image;
use SDL_image;

import SDL;
import Surface;

/**
 * For SDL_Image functions
 */
class ImageLoader {

	static func load(String fileName) -> Surface {
	
		SDL_Surface* tmp = IMG_Load(fileName);
 
		if(!tmp) {
		  
			fprintf(stderr, "Error: '%s' could not be opened: %s\n", fileName, SDL.getError);
			return null;

		}
		
		return new Surface(tmp);
	
	}

}
