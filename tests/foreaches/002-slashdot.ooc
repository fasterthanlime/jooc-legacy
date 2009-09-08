Float: cover from float {

	print: func { printf("Value = %f\n", this) }

}

func main {

	x = 1.0, y = 4.2, z = 3.7 : Float
	printf("Assigned to values (%f, %f, %f)\n", x, y, z);
	[x, y, z]\.print; // slashdot syntax =)

}
