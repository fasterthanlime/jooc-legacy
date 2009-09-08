Float: cover from float {

	print: func { printf("Value = %f\n", this) }

}

main: func {

	list := List<Float> new()
	list add(0.3)
	list add(3.14)
	list add(5.72)
	list\ print() // slashdot syntax =)

}
