include stdio;

cover String from char*;
extern func printf(String, ...);

func call {
	printf("Hey, here's MyClass =)\n");
}
