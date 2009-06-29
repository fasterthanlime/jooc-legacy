import structs.Array;

func main(Int argc, String[] argv) {

	for(String arg: new Array(argc, argv)) {
		printf("Got arg %s\n", arg);
	}

}
