include stdio, unistd;
import os.Timer;

func main {

	Timer timer = new Timer;
	printf("sleeping a while...\n");
	for(Int i: 0..10000) {
		usleep(3);
	}
	printf("we slept for %ld milli-seconds\n", timer.millis);

}
