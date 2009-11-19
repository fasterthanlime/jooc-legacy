import Pipe, unistd, wait
import structs/ArrayList

SubProcess: class {

    args: ArrayList<String>
    executable: String
    stdOut = null: Pipe
    stdIn  = null: Pipe
    stdErr = null: Pipe
    buf :String*
    stdoutPipe: Pipe 
    init: func(=args) {
        this executable = this args get(0)
        this args add(null) // execvp wants NULL to end the array
        buf = this args toArray() // ArrayList<String> => String*
    } 
    setStdout: func(=stdOut){}
    setStdin:  func(=stdIn) {}    
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
            execvp(executable, buf)
       
        } else {
            waitpid(-1, status&, null)
            if (WIFEXITED(status)) {
                result = WEXITSTATUS(status)
            }
        }
        return result
    }

}
