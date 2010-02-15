import os/Socket
import text/StringBuffer

main: func {
    socket := DatagramSocket new()
    socket bind("127.0.0.1", 2000)

    data := Buffer new(100)
    while (true) {
        bytesRecv := socket receive(data)
        "Received %i bytes" format(bytesRecv) println()
        "Content: %s" format(data toString) println()
    }
}
