include stdio;
cover String from char*;
extern func printf(String, ...);

cover Float from float {

	func print printf("Value = %f\n", this);

}

func main {

	List<Float> list = new;
	for(Float f: (0.3, 3.14, 5.72)) list.add(f);
	list/.print; // slashdot syntax =)

}
