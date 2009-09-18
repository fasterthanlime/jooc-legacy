include math

sqrt: extern func (Float) -> Float

Point3f: cover {

	x, y, z: Float

	new: static func (.x, .y, .z) -> This {
		point: Point3f
		point x = x
		point y = y
		point z = z
		return point
	}
	length: func -> Float sqrt(squaredLength())
	squaredLength: func -> Float (x * x + y * y + z * z)

}

main: func {

	point := new Point3f(3.0, 1.2, 5.5)
	printf("Point (%f, %f, %f), length = %f\n", point x, point y, point z, point length());

}
