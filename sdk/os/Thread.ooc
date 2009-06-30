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
		// Feinte du loup des bois. We pass a pointer to ourselves
		// to pthread_create so that it corresponds to the 'this' argument
		// of our member method. Easy enough, huh ?
		return  pthread_create (&thread, NULL, @rrun, this);	
	}
	
	func wait -> Int {
		return pthread_join(thread, NULL);
	}

}

