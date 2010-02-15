import os/Socket

main: func {
    socket := StreamSocket new()
    socket bind(2000)
    socket listen(10)

    while(true) {
        conn = socket accept()
        conn output() send("Hello mysterious client!")
        conn close()
    }
}
