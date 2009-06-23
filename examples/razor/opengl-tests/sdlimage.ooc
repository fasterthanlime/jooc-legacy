import lang.String;
import structs.Array;

import sdl.SDL;
import sdl.WM;
import sdl.Surface;
import sdl.ImageLoader;

func main(Int argc, String[] argv) {

    SDL.init(SDL.INIT_VIDEO);
	
	Surface screen = SDL.setVideoMode(450, 600, 32, SDL.HWSURFACE);
    WM.setCaption("ooc + SDL = awesome");

    Surface logo = ImageLoader.load("youngwoman.jpg");
	if(!logo) return 1;
	
	screen.draw(logo, 0, 0);
    screen.flip; //Refresh the screen

	if(argc <= 2 || argv[1].equals("--test")) {
		pause;
    }

	SDL.quit;
    return EXIT_SUCCESS;

}

func pause {

    Bool running = true;
    SDL_Event event;
 
    while(running) {
		
        SDL.waitEvent(&event);
        switch(event.type) {
            case SDL_QUIT:
				running = false;
        }
		
    }
}
