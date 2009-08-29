include memory, string

String: cover from Char* {
	
	replace: func (oldie, kiddo: Char) -> String {
		copy := clone()
		for(i: Int in 0..this length()) {
			if (copy[i] == oldie) copy[i] = kiddo
		}
		return copy
	}

	//length: func -> Int strlen(this)
	
	clone: func -> String {
		//length := this length()
		length := length()
		copy := GC_malloc(length) as String
		memcpy(copy, this, length)
		return copy
	}

}

main: func {

	printf("doogy-di-doo is not %s\n", "doogie-die-doo" replace('d', 'b'))

}
