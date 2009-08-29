include stdio;

func main {

	Int r = 0xff000000;
	Int g = 0x00ff0000;
	Int b = 0x0000ff00;
	Int a = 0x000000ff;

	printf("(r, g, b, a) = (%d, %d, %d, %d)\n", r, g, b, a);
	printf("(r, g, b, a) = (%x, %x, %x, %x)\n", r, g, b, a);

}
