include stdio;

func main {
	print(24);
}

func print(Int value) {
	print(value, "or 42 ?");
}

func print(Int value, String message) {
	printf("%d %s\n", value, message);
}
