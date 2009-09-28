include sys/stat
include sys/types
include stdio
include stdlib
include fcntl

import mmap
import wait
import unistd

//import structs/HashMap
O_RDWR: extern Int
O_RDONLY: extern Int
O_WRONLY: extern Int

/*
stdout: extern Int
freopen: extern func(String, String, FILE*) -> FILE*
fclose: extern func(FILE*)
*/
open: extern func(String, Int) -> Int
write: extern func(Int, String, Int)
read: extern func(Int, Pointer, Int) -> Int

close: extern func(Int)

Pipe: class {

    readFD:  Int 
    writeFD: Int 

    init: func() {
       
        fds := [0, 0]
        /* Try to open a new pipe */
        if (pipe(fds) < 0) {
            "Error in creating the pipe" println()
        }
        readFD  = fds[0]
        writeFD = fds[1]
    }
    read: func(len: Int) -> Pointer {
        buf := gc_malloc(PIPE_BUF)
        read(readFD, buf, PIPE_BUF-1) // TODO: use a loop if len > PIPE_BUF
        return buf
    }
    
    write: func(arg: Pointer, len: Int) {
        write(writeFD, arg, len)
    }

    close: func(arg: Char) {
        if (arg == 'r') {
            close(readFD)
        } else if (arg == 'w') {
            close(writeFD)
        }
    }
}          


SubProcess: class {

    args: String* 
    executable: String
       init: func(=args) {executable=args[0]}
     
    execute: func -> Int {
        pid := fork()    
        status: Int
        stdoutPipe := Pipe new() // BUG: outFD: Int[] doesn't work
        stderrPipe := Pipe new()
       
        
        result := 42 // default value and sense of life ;) 
        if (pid == -1) {
            /* pid < 0 signals error */
            "Error in forking" println()
            "Exit" println() 
            x := 0
            x = 10 / x
            /*replace with real real exceptions */
        } else if (pid == 0) {
            
            /* in child-process */
            
            if (dup2(stdoutPipe readFD, 1) == -1) { // Redirecting stdout to the pipe
                "error" println()
            }
            if (dup2(stdoutPipe readFD, 2) == -1) { // Redirecting stderr to the pipe
                "error" println()
            }
            
            // TODO: use constants instead of magic numbers + refactoring
            
            stdoutPipe close('r')
            execvp(executable, args)
        } else {
            /* In parent-process */
            
            stdoutPipe close('r')
            waitpid(0, status&, null)
            tmp := stdoutPipe read(20)
            tmp as String println()                         
            if (tmp) {
                "hallo" println()
            }
            stdoutPipe write("hallo", 5)
             
            if (WIFEXITED(status)) {
               result = WEXITSTATUS(status) 
            } 
        }
        return result
    }
    
            
}


