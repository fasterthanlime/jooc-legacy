include stdio;
cover String from char*;
cover Int from int;
extern func printf(String, ...);

cover Float from double {
	func print printf("Value = %f\n", this);
}

func main {

	Float x = 1.0, y = 4.2, z = 3.7;
	printf("Assigned to values (%f, %f, %f)\n", x, y, z);
	(x, y, z)\.print; // slashdot syntax =)

}
