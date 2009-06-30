include unistd;

import os.Time;

func main {

	printf("Waiting 1.5 seconds\n");
	fflush(stdout);
	Time.sleepSec(1.5f);
	//usleep(1_000_000);
	printf("Done!\n");

}
