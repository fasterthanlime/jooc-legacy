include stdio, stdlib, string;

func main(Int argc, String[] argv) {
	
	if(argc <= 1) {
		printf("usage: %s [-double] <n>\n", argv[0]);
		exit(0);
	}
	
	if(strcmp(argv[1], "-double") == 0) {
		printf("res = %lld\n", fac2(atoll(argv[2])));
	} else {
		printf("res = %lld\n", fac(atoll(argv[1])));
	}
	
}

func fac(LLong n) -> LLong {
	
	if(n <= 1) {
		return 1;
	} else {
		return n * fac(n - 1);
	}
	
}

func fac2(LLong n) -> LLong {
	
	if(n <= 2) {
		return 1;
	} else {
		return n * fac(n - 2);
	}
	
}
