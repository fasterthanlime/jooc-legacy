include stdio;

import structs.Stack;

func main(Int argc, String[] argv) {

	Stack stack = new;
	
	String test_string = "Foo";
	Int test_int = -420;
	UInt test_uint = 69;
	Double test_double = 3.1337;
	
	printf("Pushing a String, Int, UInt, and Double...\n");
	stack.push(test_string);
	stack.push((Object)test_int);
	stack.push((Object)test_uint);
	stack.push(&test_double);
	
	printf("Should peek a Double:\t%f\n", *((Double*)stack.peek()));
	
	printf("Should pop a Double:\t%f\n", *((Double*)stack.pop()));
	printf("Should pop a UInt:\t%d\n", (UInt)stack.pop());
	printf("Should pop an Int:\t%d\n", (Int)stack.pop());
	printf("Should pop a String:\t%s\n", (String)stack.pop());
	
	return 0;
}
