import structs/Array
import os/Process

main: func (args: Array<String>) {
	
	if(args size() <= 1) {
		println("Usage: ")
	}
	
	p := Process new(["mplayer", args get(1), null])
	exitCode := p execute()
	println("Process ended with exit code " + exitCode)
	return exitCode
	
}
