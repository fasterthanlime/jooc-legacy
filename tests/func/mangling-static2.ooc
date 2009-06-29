include stdio;

func main {
	print(42);
	print(3.14f);
}

static func print(Int intValue) {
	printf("Int: %d\n", intValue);
}

static func print(Float floatValue) {
	printf("Float: %.2ff\n", floatValue);
}
