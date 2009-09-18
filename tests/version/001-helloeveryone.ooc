include stdio

func main {

	version(linux) {
		printf("Hello, Linux =)");
	}
	version(apple) {
		printf("Hello, Mac =)");
	}
	version(windows) {
		printf("Hello, Windows =)");
	}
	version(!linux, !apple, !windows) {
		printf("Hi, stranger ;)");
	}

}
