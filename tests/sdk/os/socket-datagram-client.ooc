import os/Socket
import text/StringBuffer

main: func {
    socket := DatagramSocket new()

    data := Buffer new("Hello!")
    bytesSent := socket send("127.0.0.1", 2000, data)
    "Sent %i bytes to server" format(bytesSent) println()
}
