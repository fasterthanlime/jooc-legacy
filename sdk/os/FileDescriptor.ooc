include fcntl
import  unistd

open:  extern func(String, Int) -> Int
write: extern func(FileDescriptor, Pointer, Int) -> Int
read:  extern func(FileDescriptor, Pointer, Int) -> Int
close: extern func(FileDescriptor) -> Int

FileDescriptor: cover from Int {

    write: func(data: Pointer, len: Int) -> Int{
        result := write(this, data, len)
        //_errMsg(result, "write")
        return result
    }
    
    read: func(len: Int) -> Pointer {
        buf := gc_malloc(len)
        /*result :=*/ read(this, buf, len)
        //_errMsg(result, "read")
        return buf
    }

    close: func() -> Int{
        result := close(this)
        //_errMsg(result, "close")
        return result
    }
    
    /*    
    dup2: func(fd: FileDescriptor) -> FileDescriptor {
        return dup2(This, fd)
    }
    */
    
    _errMsg: func(var: Int, funcName: String) {
        if (var < 0) {
            printf("Error in FileDescriptor : %s\n", funcName)
        }
    }
}

    
        
        

