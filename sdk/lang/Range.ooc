import lang/Int

Range: cover {

	min, max: Int
	
	new: static func (.min, .max) -> This {
		this : This
		this min = min
		this max = max
		return this
	}

}
