import os/Socket
import text/StringBuffer

main: func {
    socket := StreamSocket new()
    socket connect("127.0.0.1", 2000)

    buffer := BufferWriter new(1000)
    buffer write(socket input())

    socket close()
}
