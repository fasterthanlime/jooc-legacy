import text/Buffer

main: func {
    dest := BufferWriter new()
    source := BufferReader new()
    source buffer() append("If you can read this then the transfer worked!")

    dest write(source)
    dest buffer() toString() println()
}
