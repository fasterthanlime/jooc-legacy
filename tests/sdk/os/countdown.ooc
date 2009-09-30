import os/Time

main: func {

	"seconds = " print()
	seconds := stdin readLine() toInt()

	"go!" println()
	while(seconds) {
		(seconds + " seconds left.") println()
		seconds -= 1
		Time sleepSec(1) // sleep 1 second
	}
	"time's up!" println()

}
