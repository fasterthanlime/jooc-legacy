import unistd
import Pipe

test: func() {

   fd := [-1, -1]
   pipe(fd)
   pid := fork()
   if (pid == 0) {
       close(fd[0])
       dup2(fd[1], 1)
       close(fd[1])
       execvp("/bin/ls", ["/bin/ls", ".", null])
       
   } else {
       close(fd[1])
       buf :Pointer
       buf = gc_malloc(100)
       read(fd[0], buf, 20)
       //buf as String println()
       "hallo" println()
   }
}
main: func() {
    test()
}




   
