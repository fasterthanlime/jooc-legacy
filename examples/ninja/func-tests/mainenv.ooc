import structs.Array;

func main(Int argc, String[] argv, String[] envp) {

	for(String arg: new Array(argc, argv)) {
		printf("Got arg '%s'\n", arg);
	}

	Array enva = Array.nullTerminated(envp);
	for(String env: enva) {
		printf("Got env '%s'\n", env);
	}

}
