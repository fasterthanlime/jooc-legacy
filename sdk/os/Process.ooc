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
        executeNoWait()
        wait()
    }

    /** Wait for the process to end. Bad things will happen if you haven't called `executeNoWait` before. */
    wait: func -> Int {
        status: Int
        result := -555
        if(stdIn != null) {
            stdIn close('w')
        }
        waitpid(-1, status&, null)
        if (WIFEXITED(status)) {
            result = WEXITSTATUS(status)
            if (stdOut != null) {
                stdOut close('w')
            }
            if (stdErr != null) {
                stdErr close('w')
            }
        }    
        return result
    }

    /** Execute the process without waiting for it to end. You have to call `wait` manually. */
    executeNoWait: func {
        pid := fork()
        if (pid == 0) {
            if (stdIn != null) {
                stdIn close('w')
                dup2(stdIn readFD, 0)
            }
            if (stdOut != null) {
                stdOut close('r')
                dup2(stdOut writeFD, 1)
            }
            if (stdErr != null) {
                stdErr close('r')
                dup2(stdErr writeFD, 2)
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
        }
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

    /** 
     * Send `data` to the process, wait for the process to end and get the
     * stdout and stderr data. You have to do `setStdIn(Pipe new())`/
     * `setStdOut(Pipe new())`/`setStdErr(Pipe new())`
     * before in order to send / get the data. You have to run `executeNoWait` before.
     * You can pass null as data, stdoutData or stderrData.
     */
    communicate: func (data: String, stdoutData, stderrData: String*) -> Int {
        /* send data to stdin */
        if(data != null) {
            written := 0
            while(written < data length())
                written += stdIn write(data)
        }
        /* wait for the process */
        result := wait()
        /* get the data */
        if(stdoutData != null)
            stdoutData@ = PipeReader new(stdOut) toString()
        if(stderrData != null)
            stderrData@ = PipeReader new(stdErr) toString()
        result
    }
}
