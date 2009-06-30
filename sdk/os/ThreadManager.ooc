/**
* Thread manager is used to synchronise threads starts
* @author Patrice Ferlet <metal3d@gmail.com>
*/

import os.Thread;
import structs.ArrayList;

/**
 * ThreadManager implements ArrayList to append threads
 * It is used to start many Threads and waiting end of processes
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
