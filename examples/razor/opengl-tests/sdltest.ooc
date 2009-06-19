import structs.Array;
import lang.String;

include stdlib, stdio, SDL/SDL;
use SDL, gl;
 
ctype SDL_Event;

func main(Int argc, String[] argv) {

    Array args = new(argc, argv);

    SDL_Init(SDL_INIT_VIDEO);
 
    SDL_SetVideoMode(640, 480, 32, SDL_OPENGL);


    if(args.size >= 2) {
        String arg = args.get(1);
        if(arg.equals("--test")) {
            quit;
        }
    }

    pause;

}

func pause {

    Int continuer = 1;
    SDL_Event event;
 
    while (continuer)
    {
        SDL_WaitEvent(&event);
        switch(event.type)
        {
            case SDL_QUIT:
                continuer = 0;
        }
    }
}

func quit {

    SDL_Quit();

    exit(EXIT_SUCCESS);

}
