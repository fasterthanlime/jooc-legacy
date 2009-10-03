import Pipe, unistd, wait

SubProcess: class {

    args: String* 
    executable: String
    stdOut = null: Pipe
    stdIn  = null: Pipe
    stdErr = null: Pipe
    stdoutPipe: Pipe 
    init: func(=args) {executable = args[0]}
    
    setStdout: func(=stdOut){}
    setStdin: func(=stdIn) {}    
    setStderr: func(=stdErr) {}
    
    execute: func -> Int {
        status: Int
        result := -555
        pid := fork()
        if (pid == 0) {
            if (stdOut != null) {
                stdOut close('r')
                dup2(stdOut writeFD, 1)
            }
            execvp(executable, args)
       
        } else {
            waitpid(-1, status&, null)
            if (WIFEXITED(status)) {
                result = WEXITSTATUS(status)
            }
        }
        return result
    }

}
main: func() {
   
    
    process := SubProcess new(["/bin/ls",".", null])
    myPipe := Pipe new()
    process setStdout(myPipe)
    process execute()
    myPipe read(20) as String println()
    
    
}

