Vector2f: cover {
	x, y: Float

	new: static func ~two (.x, .y) -> This {
        this: This
        this x = x
        this y = y
        return this
    }
}

Vector3f: cover extends Vector2f {
	z: Float

	new: static func ~three (.x, .y, .z) -> This {
        this: This
        this x = x
        this y = y
        this z = z
        this
    }
}

main: func {

	Vector2f new(1, 2)
	Vector3f new(4, 5, 6)

}
