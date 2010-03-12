import Buffer

StringReader: class extends BufferReader {
    init: func ~withString (string: String) {
        this(Buffer new(string))
    }
}
