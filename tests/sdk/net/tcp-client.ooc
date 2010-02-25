import net/StreamSocket

main: func {
    socket := StreamSocket new("localhost", 2800)
    socket remote toString() println()
    socket connect()

    bytesSent := socket send("hi server!")
    "Sent %d bytes!" format(bytesSent) println()

    buffer := String new(100)
    bytesRecv := socket receive(buffer, 100)
    "Recv %d bytes!" format(bytesRecv) println()
    "Data = '%s'" format(buffer) println()

    socket close()
}
