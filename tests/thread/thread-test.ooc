import os.ThreadManager;
import os.Time;

/**
* MyThread implements Thread
* MUST implement "run" method
*/
class MyThread from Thread {

	String name;
	static Int counter = 1;

	func new(=name) {
		super();
	}

	func new {
		super();
		name = malloc(30);
		sprintf(name, "Thread %d", counter++);
	}

	func run{
		srand ( time (null) );
		for (Int i: 0..5) {
			Float sl =  rand () % 5;
			sl /= 3;	
			Time.sleepSec( sl );
			printf("Thread %s, iterator %d, wait: %2f\n",name,i,sl);
		}
	}
}


func main {

	printf("Let's go \n");

	//the thread manager
	ThreadManager manager = new;
	
	//append 2 threads
	MyThread t1 = new;
	MyThread t2 = new;

	manager.add(t1);
	manager.add(t2);

	//start threads
	manager.start();
	
}
