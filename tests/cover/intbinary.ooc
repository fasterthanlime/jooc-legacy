import lang.Int;
include stdio;

func main {

	Int beer = 1664;
	printf("Binary representation of beer: %s\n", beer.binaryRepr);

	printf("Counting to ten in binary:\n");
	for(Int i: 1..11) {
		printf("%d = %s\n", i, i.binaryRepr);
	}

}
