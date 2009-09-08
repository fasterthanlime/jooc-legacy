Float: cover from float {

	print: func { printf("Value = %f\n", this) }

}

main: func {

	list := List<Float> new()
	for(f in [3.9, 4.2, 9.0]) list add(f)
	list\ print() // slashdot syntax =)

}
