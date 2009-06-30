/**
* POSIX Thread creator. You only have to implement "run" method
* @author Patrice Ferlet <metal3d@gmail.com>
*/
use pthread;
include stdlib,stdio, pthread;
typedef pthread_t Pthread;

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

