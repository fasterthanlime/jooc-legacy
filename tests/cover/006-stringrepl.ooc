include memory, string

MyString: cover from Char* {
	
	replace: func (oldie, kiddo: Char) -> This {
		copy := clone()
		for(i in 0..this length()) {
			if (copy[i] == oldie) copy[i] = kiddo
		}
		return copy
	}

	length: func -> Int strlen(this)
	
	clone: func -> This {
		length := length()
		copy := gc_malloc(length) as This
		memcpy(copy, this, length)
		return copy
	}

}

main: func {

	printf("doogie-di-doo is not %s\n", "doogie-die-doo" as MyString replace('d', 'b'))

}
