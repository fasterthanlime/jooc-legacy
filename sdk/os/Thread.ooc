use pthread;
include stdlib;
include stdio;
include pthread;
import structs.ArrayList;

typedef pthread_t Pthread;


/**
 * ThreadManager implements ArrayList to append threads
 */
class ThreadManager from ArrayList {

	func new;
	func start {
		for (Thread managed : this) {
			managed.start;
		}
		for (Thread managed : this) {
			managed.wait;
		}
	}
}



/**
 * Thread class
 */
abstract class Thread {

	//Pthread will stock pthread reference
	Pthread thread;

	func new;

	/**
 	* This is the method to implement wich will be run has thread
 	*/
	abstract func run;

	func rrun{
		run;
	}

	func start -> Int {
		// Super duper astuce to make C and ooc play nice together
		return  pthread_create (&thread, NULL, @rrun, this);	
	}
	
	func wait -> Int {
		return pthread_join(thread, NULL);
	}

}

