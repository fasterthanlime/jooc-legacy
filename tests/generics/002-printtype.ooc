Dog: class {}
Hound: class extends Dog {}

main: func {
	printType(new Dog as Object)
	printType(new Hound as Object)
}

printType: func(o: Object) {
	printf("Class hierarchy = ")
	c := o class
	while (c) {
		printf("%s", c name)
		c = c super
		if(c) printf(" -> ")
	}
	printf("\n-----------------\n");
}
