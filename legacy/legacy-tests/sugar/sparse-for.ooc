include stdio;

func main {

	for(Int i: 0..101, 10) {
		printf("%d, ", i);
		fflush(stdout);
	}
	printf(".. were 0 to 100, with step 10\n");

}
