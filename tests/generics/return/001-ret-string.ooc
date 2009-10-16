Ahem: class <T> {
	
	wacko: func -> T {
		w := String new(6)
		w[0] = 'w';
		w[1] = 'a';
		w[2] = 'c';
		w[3] = 'k';
		w[4] = 'o';
		w[5] = '\0';
		
		val : T
		val = w
		return val
	}
	
	woko: func -> T {
		return wacko()
	}
	
}


main: func {
	ah := Ahem<String> new()
	wok := ah woko()
	wok println()
}
