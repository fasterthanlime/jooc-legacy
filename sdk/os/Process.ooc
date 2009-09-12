include stdio
include sys/types
include sys/wait
include unistd

Pid_T: cover from int

fork: extern func -> Pid_T
execv: extern func(String, String*) -> Int
execvp: extern func(String, String*) -> Int
execve: extern func(String, String*, String*) -> Int
wait: extern func(Int*) -> Int
waitpid: extern func(Pid_T, Int*, Int) -> Int

WEXITSTATUS: extern func (Int) -> Int
WIFEXITED: extern func (Int) -> Int



//null = '\0': String
SubProcess: class {

    args: String*
    //status: Int*
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
            /* TODO: Replace with real real exceptions */
        } else if (pid == 0) {
            /* in child-process */
            execvp(executable, args)
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

/* example
 
main: func{
   
    b := SubProcess new(["/bin/ls",".", null])
    printf("%d\n", b execute())
    
}
*/
