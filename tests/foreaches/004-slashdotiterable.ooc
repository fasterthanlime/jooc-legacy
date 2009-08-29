include stdio;
cover String from char*;
extern func printf(String, ...);

cover Float from float {

	func print printf("Value = %f\n", this);

}

func main {

	List<Float> list = new;
	list.add(0.3);
	list.add(3.14);
	list.add(5.72);
	list/.print; // slashdot syntax =)

}
