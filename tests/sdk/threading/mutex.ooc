import threading/[Runnable, Thread, Mutex]

global: Mutex

LazyWorker: class extends Runnable {
    run: func {
        // Wait around to aquire a lock on global...
        global lock()

        "Got a lock, but I don't want to work, so releasing..." println()
        global unlock()
    }
}

main: func {
    global = Mutex new()

    Thread new(LazyWorker new()) start()
    Thread new(LazyWorker new()) start()
    Thread new(LazyWorker new()) start()
}
