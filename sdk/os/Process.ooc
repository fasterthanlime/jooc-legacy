include sys/stat
include sys/types

include fcntl

import mmap
import wait
import unistd

O_RDWR: extern Int
O_RDONLY: extern Int
O_WRONLY: extern Int


stdout: extern Int
freopen: extern func(String, String, FILE*) -> FILE*
fclose: extern func(FILE*)

open: extern func(String, Int) -> Int
write: extern func(Int, String, Int)
close: extern func(Int)
SubProcess: class {

    args: String* 
    executable: String
       init: func(=args) {executable=args[0]}
     
    execute: func -> Int {
        pid := fork()    
        status: Int
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
            puffer := "hallo"
            "blub" println()
            //dup2(stdout, a)
            a := freopen("huhu", "w", stdout)
            execvp(executable, args)
            fclose(a)
            //"hallo" println()
            //write(a, puffer&, 5)
            //close(a)
                                    
        } else {
            /* In parent-process */
            waitpid(0, status&, null)
            if (WIFEXITED(status)) {
               result = WEXITSTATUS(status) 
            } 
        }
        return result
    }
        
}


 
main: func{
   
    b := SubProcess new(["/bin/ls",".", null])
    printf("%d\n", b execute())
    
}

