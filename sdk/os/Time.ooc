include time, unistd;

typedef time_t TimeType;
typedef struct tm* TimeStruct;

class Time {
	
	static func sec -> Int {
		TimeType tt = time(null);
		TimeStruct val = localtime(&tt);
		return val->tm_sec;
	}
	
	static func min -> Int {
		TimeType tt = time(null);
		TimeStruct val = localtime(&tt);
		return val->tm_min;
	}
	
	static func hour -> Int {
		TimeType tt = time(null);
		TimeStruct val = localtime(&tt);
		return val->tm_hour;
	}
	
	static func sleepSec(Float duration) {
		usleep((UInt) (duration * 1_000_000f));
	}

	static func sleepMilli(UInt duration) {
		usleep((UInt) (duration * 1_000));
	}

	static func sleepMicro(UInt duration) {
		usleep((UInt) duration);
	}

}
