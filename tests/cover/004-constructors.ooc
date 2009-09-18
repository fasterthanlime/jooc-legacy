include math

sqrt: extern func (Float)

Point3f: cover {

	x, y, z: Float

	new: static func (.x, .y, .z) -> This {
		return [x, y, z] as Point3f
	}
	length: func -> Float sqrt(squaredLength())
	squaredLength: func -> Float (x * x + y * y + z * z)

}

main: func {

	point := new Point3f(3.0, 1.2, 5.5)
	printf("Point (%f, %f, %f), length = %f\n", point x, point y, point z, point length())

}
