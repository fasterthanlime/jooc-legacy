include fcntl
import  unistd

open: extern func(String, Int) -> Int
write: extern func(Int, Pointer, Int) -> Int
read: extern func(Int, Pointer, Int) -> Int
 
close: extern func(Int) -> Int

FileDescriptor: cover from Int {

    write: func(data: Pointer, len: Int) -> Int{
        result := write(This, data, len)
        //_errMsg(result, "write")
        return result
    }
    
    read: func(len: Int) -> Pointer {
        buf := gc_malloc(len)
        result := read(This, buf, len)
        //_errMsg(result, "read")
        return buf
    }

    close: func() -> Int{
        result := close(This)
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

    
        
        

