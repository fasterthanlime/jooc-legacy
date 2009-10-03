import unistd
import FileDescriptor

include fcntl
include sys/stat
include sys/types


open: extern func(String, Int) -> Int
write: extern func(Int, String, Int)
read: extern func(Int, Pointer, Int) -> Int
 
close: extern func(Int) -> Int

Pipe: class  {
    readFD:  FileDescriptor 
    writeFD: FileDescriptor 
    
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
        return readFD read(len)
    }
    
    write: func(data: Pointer, len: Int) -> Int{
        return writeFD write(data, len)
    }

    close: func(arg: Char) -> Int{
        result :Int
        if (arg == 'r') {
            result = readFD close()
        } else if (arg == 'w') {
            result = writeFD close()
        } else {
            "unkown argument" println()
            result = -666
        }
        return result
    }
}          


