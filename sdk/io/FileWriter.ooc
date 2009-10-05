import io/Writer, io/File

fopen: extern func(filename: Char*, mode: Char*) -> FILE*
fwrite: extern func(ptr: Pointer, size: SizeT, count: SizeT, file: FILE*) -> SizeT
SEEK_CUR: extern func
SEEK_SET: extern func
SEEK_END: extern func
ftell: extern func(stream: FILE*) -> Long
 
FileWriter: class extends Writer {

    file: FILE*
    
    init: func ~withFile (fileObject: File, append: Bool) {
        init (fileObject getPath(), append)
    }

    init: func ~withFileOverwrite (fileObject: File) {
        init(fileObject, false) 
    }
    
    init: func ~withName (fileName: String, append: Bool) {
        file = fopen(fileName, append ? "a" : "w");
        if (!file) 
            Exception new(This, "File not found: " + fileName) throw()
    }

    init: func ~withNameOverwrite (fileName: String) {
        init(fileName, false)
    }

    write: func(chars: String, length: SizeT) -> SizeT {
        fwrite(chars, 1, length, file)
    }
}
