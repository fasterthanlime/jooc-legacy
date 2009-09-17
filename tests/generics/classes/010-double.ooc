Container: class <T> {

	init: func {
		// we can't retrieve the nested type parameter 'Int'!
		printf("Got type %s<%s>>\n", this class name, T name)
	}

}

main: func {

	cont := Container<Container<Int>> new()
	cont2 := Container<Container<Int> > new()
	cont3 := Container < Container < Int > > new ( )


}
