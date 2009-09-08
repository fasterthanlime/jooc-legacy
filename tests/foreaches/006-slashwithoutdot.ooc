Float: cover from float {

	print: func { printf("Value = %f\n", this) }

}

main: func {

	list := List<Float> new()
	list add([3.9, 4.2, 9.0]\)
	list\ print() // slashdot syntax =)

}
