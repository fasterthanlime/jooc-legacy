include stdio;

class Mine {
	
	Int i;
	
	new(=i);

	func j -> Int {
		i + 3;
	}
	
}

func main {
	Mine m = new Mine(6);
	printf("i = %d, j = %d\n", m.i, m.j);
}
