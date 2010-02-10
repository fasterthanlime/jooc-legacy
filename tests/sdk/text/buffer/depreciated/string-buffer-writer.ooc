import io/Writer
import text/StringBuffer

/**
    Note: StringBuffer has been depreciated. Use BufferWriter instead.
*/

write: func (writer: Writer) {
    for(i in 0..10) {
        writer write("yay =D ")
    }
}

main: func {
    sb := StringBuffer new()
    write(sb)
    sb toString() println()
}
