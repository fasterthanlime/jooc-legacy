use SDL;
include SDL/SDL;

import Surface;

ctype SDL_Event;
ctype SDL_Surface;
ctype SDL_Rect;

class SDL {
	
	/** Initialization flags */
	const static UInt INIT_TIMER       = SDL_INIT_TIMER;
	const static UInt INIT_AUDIO       = SDL_INIT_AUDIO;
	const static UInt INIT_VIDEO       = SDL_INIT_VIDEO;
	const static UInt INIT_CDROM       = SDL_INIT_CDROM;
	const static UInt INIT_JOYSTICK    = SDL_INIT_JOYSTICK;
	const static UInt INIT_EVERYTHING  = SDL_INIT_EVERYTHING;
	const static UInt INIT_NOPARACHUTE = SDL_INIT_NOPARACHUTE;
	const static UInt INIT_EVENTTHREAD = SDL_INIT_EVENTTHREAD;
	
	/** Screen formats */
	const static UInt SWSURFACE  = SDL_SWSURFACE;
	const static UInt HWSURFACE  = SDL_HWSURFACE;
	const static UInt ASYNCBLIT  = SDL_ASYNCBLIT;
	const static UInt ANYFORMAT  = SDL_ANYFORMAT;
	const static UInt HWPALETTE  = SDL_HWPALETTE;
	const static UInt DOUBLEBUF  = SDL_DOUBLEBUF;
	const static UInt FULLSCREEN = SDL_FULLSCREEN;
	const static UInt OPENGL     = SDL_OPENGL;
	const static UInt OPENGLBLIT = SDL_OPENGLBLIT;
	const static UInt RESIZABLE  = SDL_RESIZABLE;
	const static UInt NOFRAME    = SDL_NOFRAME;
	
	/** Alpha */
	const static UInt SRCALPHA   = SDL_SRCALPHA;
	
	
	/**
	 * The SDL_Init function initializes the Simple Directmedia Library
	 * and the subsystems specified by flags. It should be called before
	 * all other SDL functions. 
	 * 
	 * Note : Unless the SDL_INIT_NOPARACHUTE flag is set, it will install
	 * cleanup signal handlers for some commonly ignored fatal signals
	 * (like SIGSEGV). 
	 * 
	 *  List of SDL initialization flags
	 * 
	 * SDL_INIT_TIMER The timer subsystem
	 * 
	 * SDL_INIT_AUDIO The audio subsystem
	 * 
	 * SDL_INIT_VIDEO The video subsystem
	 * 
	 * SDL_INIT_CDROM The cdrom subsystem
	 * 
	 * SDL_INIT_JOYSTICK The joystick subsystem
	 * 
	 * SDL_INIT_EVERYTHING All of the above
	 * 
	 * SDL_INIT_NOPARACHUTE Prevents SDL from catching fatal signals
	 * 
	 * SDL_INIT_EVENTTHREAD Runs the event manager in a separate thread 
	 */
	static func init(UInt flags) -> Int {
		
		return SDL_Init(flags);
		
	}
	
	/**
	 * Set up a video mode with the specified width, height and bitsperpixel
	 * 
	 * @param width The desired width in pixels of the video mode to set 
	 * @param height The desired height in pixels of the video mode to set.
	 * As of SDL 1.2.10, if width and height are both 0, SDL_SetVideoMode
	 * will use the width and height of the current video mode (or the
	 * desktop mode, if no mode has been set). 
	 * @param bitsperpixel The desired bits per pixel of the video mode
	 * to set. If bitsperpixel is 0, it is treated as the current display
	 * bits per pixel. A bitsperpixel of 24 uses the packed representation
	 * of 3 bytes per pixel. For the more common 4 bytes per pixel mode,
	 * please use a bitsperpixel of 32. Somewhat oddly, both 15 and 16 bits
	 * per pixel modes will request a 2 bytes per pixel mode, but with
	 * different pixel formats. 
	 * @param flags The possible values for the flags parameter are the
	 * same used by the SDL_Surface structure. OR'd combinations of the
	 * following values are valid. 
	 * 
	 * List of flags values:
	 * 
	 * SDL.SWSURFACE Create the video surface in system memory
	 * 
	 * SDL.HWSURFACE Create the video surface in video memory
	 * 
	 * SDL.ASYNCBLIT Enables the use of asynchronous updates of the
	 * display surface. This will usually slow down blitting on single
	 * CPU machines, but may provide a speed increase on SMP systems.
	 * 
	 * SDL.ANYFORMAT Normally, if a video surface of the requested
	 * bits-per-pixel (bpp) is not available, SDL will emulate one with
	 * a shadow surface. Passing SDL.ANYFORMAT prevents this and causes
	 * SDL to use the video surface, regardless of its pixel depth.
	 * 
	 * SDL.HWPALETTE Give SDL exclusive palette access. Without this
	 * flag you may not always get the the colors you request with
	 * SDL.SetColors or SDL.SetPalette.
	 * 
	 * SDL.DOUBLEBUF Enable hardware double buffering; only valid with
	 * 
	 * SDL.HWSURFACE Calling SDL.Flip will flip the buffers and update
	 * the screen. All drawing will take place on the surface that is
	 * not displayed at the moment. If double buffering could not be
	 * enabled then SDL.Flip will just perform a SDL.UpdateRect on the
	 * entire screen.
	 * 
	 * SDL.FULLSCREEN SDL will attempt to use a fullscreen mode. If a
	 * hardware resolution change is not possible (for whatever reason),
	 * the next higher resolution will be used and the display window
	 * centered on a black background.
	 * 
	 * SDL.OPENGL Create an OpenGL rendering context. You should have
	 * previously set OpenGL video attributes with SDL.GL.SetAttribute.
	 * 
	 * SDL.OPENGLBLIT Create an OpenGL rendering context, like above, 
	 * but allow normal blitting operations. The screen (2D) surface
	 * may have an alpha channel, and SDL.UpdateRects must be used for
	 * updating changes to the screen surface.
	 * NOTE: This option is kept for compatibility only, and will be
	 * removed in next versions. It's not recommended for new code.
	 * 
	 * SDL.RESIZABLE Create a resizable window. When the window is
	 * resized by the user a SDL.VIDEORESIZE event is generated and
	 * SDL.SetVideoMode can be called again with the new size.
	 * 
	 * SDL.NOFRAME If possible, SDL.NOFRAME causes SDL to create a window
	 * with no title bar or frame decoration. Fullscreen modes automatically
	 * have this flag set.
	 * 
	 * @return null
	 * 	On error. Call SDL.getError to retrieve the last error message.
	 * @return The requested framebuffer surface
	 * 	On success. The returned surface is freed by SDL_Quit and must
	 * not be freed by the caller. This rule also includes consecutive
	 * calls to SDL.setVideoMode (i.e. resize or resolution change)
	 * because the existing surface will be released automatically.
	 * Whatever flags SDL.setVideoMode could satisfy are set in the
	 * flags member of the returned surface. 
	 */
	static func setVideoMode(Int width, Int height, Int bitsperpixel, UInt flags) -> Surface {
		
		return new Surface(SDL_SetVideoMode(width, height, bitsperpixel, flags));
		
	}
	
	/**
	 * @return a null terminated string containing information about the
	 * last internal SDL error. 
	 */
	static func getError -> String {
		
		return SDL_GetError();
		
	}
	
	static func waitEvent(SDL_Event* event) -> Int {
		
		return SDL_WaitEvent(event);
		
	}
	
	static func quit {
		
		SDL_Quit();
		
	}
	
	
}
