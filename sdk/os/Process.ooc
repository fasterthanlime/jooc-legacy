import Pipe, unistd, wait

SubProcess: class {

    args: String* 
    executable: String
    stdoutPipe: Pipe
    init: func(=args) {executable=args[0]}
        
    execute: func -> Int {
        fd := [-1, -1]
        stdoutPipe = Pipe new()
        status: Int
        result := -555
        pid := fork()
        if (pid == 0) {
       
        stdoutPipe close('r')
        //close(fd[0])
        dup2(stdoutPipe writeFD, 1)
        //dup2(fd[1], 1)
        stdoutPipe close('w')
        //close(fd[1])
        "blub" println()
        execvp(executable, args)
       
        } else {
       
            stdoutPipe close('w')
            //close(fd[1])
            buf :Pointer
            buf = gc_malloc(100)
            read(stdoutPipe readFD, buf, 20)
            //read(fd[0], buf, 20)
            stdoutPipe close('r') 
            //close(fd[0])
            buf as String println()
            waitpid(0, status&, null)

            if (WIFEXITED(status)) {
                result = WEXITSTATUS(status)
            }
        }
        return result
    }

    readStdout: func(len: Int) -> Pointer {
        return gc_malloc(29)
    }
}
main: func() {
   
    //test3()
    b := SubProcess new(["/bin/ls",".", null])
    b execute()
    b readStdout(20)
    //printf("%d\n",b execute())
    //printf("%d\n", b execute())
    //test()
    //test2()
    
}

