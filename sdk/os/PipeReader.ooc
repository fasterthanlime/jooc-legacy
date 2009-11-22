import os/Pipe

PipeReader: class {

    pipe: Pipe
    buf: String = null
    init: func(=pipe) {}

    read: func() -> String {
        return buf
    }

    hasNext: func() -> Bool {
        buf = pipe read(1) as String
        buf  != "\0"

    }
}
