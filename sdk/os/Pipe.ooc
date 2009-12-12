import unistd
import FileDescriptor

include fcntl
include sys/stat
include sys/types

open: extern func(String, Int) -> Int
write: extern func(Int, Pointer, Int) -> Int
read: extern func(Int, Pointer, Int) -> Int
 
close: extern func(Int) -> Int

Pipe: class  {
    readFD:  FileDescriptor 
    writeFD: FileDescriptor 

    init: func ~withFDs (=readFD, =writeFD) {
        if(readFD == -1) {
            fds := [-1]
            pipe(fds)
            this readFD = fds[0]
            if (pipe(fds) < 0) {
                "Error in creating the pipe" println()
            }
        }
        
        if(writeFD == -1) {
            fds := [-1]
            pipe(fds)
            this writeFD = fds[0]
            if (pipe(fds) < 0) {
                "Error in creating the pipe" println()
            }
        }
    }
    
    init: func() {
       
        fds := [-1, -1]
        /* Try to open a new pipe */
        if (pipe(fds) < 0) {
            "Error in creating the pipe" println()
        }
        readFD  = fds[0]
        writeFD = fds[1]
    }
    
    read: func(len: Int) -> Pointer {
        //return readFD read(len)
        buf := gc_malloc(len)
        read(readFD, buf, len)
        return buf
    }
    
    write: func ~string (str: String) -> Int {
        write(str, str length())
    }
    
    write: func(data: Pointer, len: Int) -> Int{
        return writeFD write(data, len)
    }

    close: func(arg: Char) -> Int{
        return match arg {
            case 'r' => readFD close()
            case 'w' => writeFD close()
            case     => -666
        }
    }
}          
