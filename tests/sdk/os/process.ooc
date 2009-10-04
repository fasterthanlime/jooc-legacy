import os/Pipe, os/Process

main: func() {

    process := SubProcess new(["/bin/ls",".", null])
    myPipe := Pipe new()
    process setStdout(myPipe)
    process execute()
    myPipe read(20) as String println()
                  
}
