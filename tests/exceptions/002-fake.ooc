Worker: class {

	goWrong: static func {
		Exception new(This, "Oh the humanity") throw()
	}

}

main: func {

	Worker goWrong()

}
