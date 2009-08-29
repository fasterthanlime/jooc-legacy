include stdio;
cover String from char*;
extern func printf(String, ...);

cover Float from float {

	func print printf("Value = %f\n", this);

}

func main {

	Float* floats = (3.9, 4.2, 9.0);
	floats/.print; // slashdot syntax =)

}
