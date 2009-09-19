include sys/stat
include sys/types
include stdio
include stdlib
include fcntl

import mmap
import wait
import unistd

import structs/HashMap
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
       
        buf := [0, 0]
        /* Try to open a new pipe */
        if (pipe(buf) < 0) {
            "Error in creating the pipe" println()
        }
        readFD  = buf[0]
        writeFD = buf[1]
    }
    read: func(len: Int) -> Pointer {
        //buf := static gc_malloc(PIPE_BUF)
        buf := Pointer
        read(readFD, buf, PIPE_BUF-1) // TODO: use a loop if len > PIPE_BUF
        return buf
    }
}          


SubProcess: class {

    args: String* 
    executable: String
       init: func(=args) {executable=args[0]}
     
    execute: func -> Int {
        pid := fork()    
        status: Int
        stdoutPipe := Pipe() // BUG: outFD: Int[] doesn't work
        stderrPipe := Pipe()
       
        
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
            /* 
            if (dup2(stdoutPipe readFD, 1) == -1) { // Redirecting stdout to the pipe
                "error" println()
            }
            if (dup2(stdoutPipe readFD, 2) == -1) { // Redirecting stderr to the pipe
                "error" println()
            }
            */
            // TODO: use constants instead of magic numbers + refactoring
            
            //close(stdoutPipe readFD)
            execvp(executable, args)
        } else {
            /* In parent-process */
            
            //close(stdoutPipe readFD)
            //waitpid(0, status&, null)
            //tmp := stdoutPipe read(20)
            /*
            if(read(errFD[0], buf, 20)) {
                "blub" println()
            }
            */
            /*
            if (tmp) {
                "hallo" println()
            }
            write(1, buf, 20)
            */ 
            if (WIFEXITED(status)) {
               result = WEXITSTATUS(status) 
            } 
        }
        return result
    }
    
            
}


 
main: func -> Int{
   
    b := SubProcess new(["/bin/ls",".", null])
    return b execute()
    
}

