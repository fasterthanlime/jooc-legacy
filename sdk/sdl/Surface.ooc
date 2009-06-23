import SDL;

class Surface {
	
	SDL_Surface* surface;
	
	new(=surface);
	
	/**
	 * On hardware that supports double-buffering, this function sets up
	 * a flip and returns. The hardware will wait for vertical retrace,
	 * and then swap video buffers before the next video surface blit
	 * or lock will return. On hardware that doesn't support
	 * double-buffering or if SDL_SWSURFACE was set, this is equivalent
	 * to calling SDL_UpdateRect(screen, 0, 0, 0, 0)
	 * 
	 * A software screen surface is also updated automatically when
	 * parts of a SDL window are redrawn, caused by overlapping windows
	 * or by restoring from an iconified state. As a result there is no
	 * proper double buffer behavior in windowed mode for a software screen,
	 * in contrast to a full screen software mode.
	 * 
	 * The SDL_DOUBLEBUF flag must have been passed to SDL_SetVideoMode, when setting the video mode for this function to perform hardware flipping.
	 * Note : If you want to swap the buffers of an initialized OpenGL
	 * context, use the function SDLGL.swapBuffers instead. 
	 */
	func flip {
		
		SDL_Flip(surface);
		
	}
	
	func setAlpha(UInt flags, UInt alpha) -> Int {
		
		return SDL_SetAlpha(surface, flags, alpha);
		
	}

	func draw(Surface srcimg, Int dx, Int dy) {
		
		draw(srcimg, 0, 0, srcimg.width, srcimg.height, dx, dy, 255);
		
	}

	func draw(Surface srcimg, Int sx, Int sy, Int sw, Int sh, Int dx, Int dy) {
		
		draw(srcimg, sx, sy, sw, sh, dx, dy, 255);
		
	}
	
	func draw(Surface srcimg, Int sx, Int sy, Int sw, Int sh, Int dx, Int dy, Int alpha) {
		
		SDL_Rect src, dst;
  
		src.x = sx;  src.y = sy;  src.w = sw;  src.h = sh;
		dst.x = dx;  dst.y = dy;  dst.w = src.w;  dst.h = src.h;

		srcimg.setAlpha(SDL.SRCALPHA, alpha);
		srcimg.blit(&src, this, &dst);
		
	}
	
	func blit(SDL_Rect* src, Surface dstimage, SDL_Rect* dst) {
		
		SDL_BlitSurface(surface, src, dstimage.surface, dst);
		
	}
	
	func width -> Int {
		
		return surface->w;
		
	}
	
	func height -> Int {
		
		return surface->h;
		
		
	}
	
}
