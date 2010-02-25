import net/ServerSocket

main: func {
    socket := ServerSocket new()
    socket bind(2800). listen(10)

    while(true) {
        conn := socket accept()
        "Got new client!" println()

        buffer := String new(100)
        bytesRecv := conn receive(buffer, 100)
        "Received %d bytes from client!" format(bytesRecv) println()
        "Data: %s" format(buffer) println()

        bytesSent := conn send("Hello client!")
        "Sent back %d bytes!" format(bytesSent) println()

        conn close()
    }
}
