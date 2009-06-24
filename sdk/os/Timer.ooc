import Time;
include sys/time;

typedef struct timeval TimeVal;

class Timer {

	TimeVal init;

	func new {
		gettimeofday(&init, null);
	}

	/**
	 * @return the time elapsed since the creation of the timer in milliseconds
	 */
	func millis -> Long {
		
		TimeVal current;
		gettimeofday(&current, null);
		
		TimeType secDiff = current.tv_sec - init.tv_sec;
		TimeType usecDiff = current.tv_usec - init.tv_usec;
		
		return secDiff * 1000 + usecDiff / 1000;
		
	}

}
