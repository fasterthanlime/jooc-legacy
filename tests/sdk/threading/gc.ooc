import threading/[Runnable, Thread]

/**
    Make sure GC operates properly in a threaded environment.
*/

Runner: class extends Runnable {
    run: func {
        s: String

        while(true) {
            s = String new(100)
        }
    }
}

main: func {
    t := Thread new(Runner new())
    t start()
}
