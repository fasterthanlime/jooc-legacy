func main {

	version(unix) {
		printf("Running on Unix\n");
	}

	version(linux) {
		printf("Running on Linux\n");
	}

	version(windows) {
		printf("Running on Windows\n");
	}

	version(beos) {
		printf("Running on BeOS\n");
	}

	version(apple) {
		printf("Running on MacOS (Apple)\n");
	}

	version(linux, windows) {
		printf("Running on Linux or Windows\n");
	}

	version(linux, !windows) {
		printf("Running on Linux or not Windows\n");
	}

	version(!linux, windows) {
		printf("Running on Windows or not Linux\n");
	}

}
