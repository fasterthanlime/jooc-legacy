import Env, Pipe, PipeReader, unistd, wait
import structs/[ArrayList, HashMap]
import text/StringBuffer

Process: class {

    args: ArrayList<String>
    executable: String
    stdOut = null: Pipe
    stdIn  = null: Pipe
    stdErr = null: Pipe
    buf : String*
    stdoutPipe: Pipe
    env: HashMap<String>
    cwd: String
    
    init: func(=args) {
        this executable = this args get(0)
        this args add(null) // execvp wants NULL to end the array
        buf = this args toArray() // ArrayList<String> => String*
        env = null
        cwd = null
    }

    init: func ~withEnv (.args, .env) {
        this(args)
        this env = env
    }
    
    setStdout: func(=stdOut){}
    setStdin:  func(=stdIn) {}    
    setStderr: func(=stdErr) {}
    setEnv: func(=env) {}
    setCwd: func(=cwd) {}
    
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
            /* amend the environment if needed */
            if(this env) {
                for(key: String in this env keys) {
                    Env set(key, env[key], true)
                }
            }
            /* set a new cwd? */
            if(cwd != null) {
                chdir(cwd)
            }
            /* run the stuff. */
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
