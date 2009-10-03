import fcntl, unistd

FileDescriptor: cover from Int {

    write: func(data: Pointer, length: Int) -> Int{
        result := write(This, data, length)
        _errMsg(result, "write")
        return result
    }
    
    read: func(len: Int) -> Pointer {
        buf := gc_malloc(PIPE_BUF)
        result := read(This, buf, len)
        _errMsg(result, "read")
        return buf
    }

    close: func() -> Int{
        result := close(This)
        //_errMsg(result, "close")
        return result
    }    
        
    dup2: func(fd: FileDescriptor) -> FileDescriptor {
        return dup2(This, fd)
    }

    _errMsg: func(var: Int, funcName: String) {
        if (var < 0) {
            printf("Error in FileDescriptor : %s\n", funcName)
        }
}

    
        
        

