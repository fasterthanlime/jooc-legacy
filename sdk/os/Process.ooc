import Pipe, PipeReader, unistd, wait
import structs/ArrayList
import text/StringBuffer

Process: class {

    args: ArrayList<String>
    executable: String
    stdOut = null: Pipe
    stdIn  = null: Pipe
    stdErr = null: Pipe
    buf : String*
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
            if (stdErr != null) {
                stdErr close('r')
                dup2(stdErr writeFD, 1)
            }
            execvp(executable, buf)
        } else {
            waitpid(-1, status&, null)
            if (WIFEXITED(status)) {
                result = WEXITSTATUS(status)
                if (stdOut != null) {
                    stdOut close('w')
                }

            }
        }
        return result
        
    }
    
    /**
     * Execute the process, and return all the output to stdout
     * as a string
     */
    getOutput: func -> String {

        stdOut = Pipe new()
        execute()
        
        result := PipeReader new(stdOut) toString()

        stdOut close('r'). close('w')
        stdOut = null
        
        result
        
    }
    
    /**
     * Execute the process, and return all the output to stderr
     * as a string
     */
    getErrOutput: func -> String {

        stdErr = Pipe new()
        execute()
        
        result := PipeReader new(stdErr) toString()

        stdErr close('r'). close('w')
        stdErr = null
        
        result
        
    }

}
