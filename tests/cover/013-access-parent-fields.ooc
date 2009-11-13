
Vector2f: cover {
	x, y: Float

	new: static func ~two (.x, .y) {}
}

Vector3f: cover extends Vector2f {
	z: Float

	new: static func ~three (.x, .y, .z) {
        this: This
        this x = x
        this y = y
        this z = z
    }
}

main: func {

	Vector2f new(1, 2)
	Vector3f new(4, 5, 6)

}
