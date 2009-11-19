import os/Pipe, os/Process
import structs/ArrayList

main: func() {

    args := ArrayList<String> new()
    args add("/bin/ls").add(".")
    
    process := SubProcess new(args)
    myPipe := Pipe new()
    process setStdout(myPipe)
    process execute()
    myPipe read(20) as String println()
                  
}
