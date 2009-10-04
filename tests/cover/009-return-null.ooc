Point: cover {
	x, y, z: Int
	
	new: static func (.x, .y, .z) -> This {
		this: This
		this x = x
		this y = y
		this z = z
		this
	}
	
	println: func {
		match {
			case equals(nullPoint) => "Yay, I'm null!" println()
			case => "No I'm not null!" println()
		}
	}
	
	equals: func (other: This) -> Bool {
		return (this x == other x) && (this y == other y) && (this z == other z)
	}
}

nullPoint : Point
nullPoint = Point new(0, 0, 0)

getNullPoint: func -> Point { nullPoint }

main: func {
	
	p := getNullPoint()
	p println()
	
}
