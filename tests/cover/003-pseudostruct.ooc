include math

sqrt: extern func (Float)

Point3f: cover {

	x, y, z: Float
	length: func -> Float sqrt(squaredLength())
	squaredLength: func -> Float (x * x + y * y + z * z)

}

main: func {

	point : Point3f
	point x = 3.0
	point y = 1.2
	point z = 5.5
	printf("Point (%.2f, %.2f, %.2f), length = %.4f\n", point x, point y, point z, point length())

}
