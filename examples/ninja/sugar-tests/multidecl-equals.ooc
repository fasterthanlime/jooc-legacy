include stdio;

func main {

	new Loque(42, 69, 1337);

}

class Loque {

	Int death, sleep, food;

	new(=death, =sleep, =food) {
		printf("It worked. (death %d, sleep %d, food %d)\n", death, sleep, food);
	}

}
