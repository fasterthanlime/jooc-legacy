import io/Reader
import text/Buffer

read: func (reader: Reader) {
    while (reader hasNext()) {
        printf("%c", reader read())
    }
    printf("\n")
}

main: func {
    sb := BufferReader new()
    sb buffer() append("yay =D yay =D yay =D")
    read(sb)
}
