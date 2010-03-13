import threading/[Runnable, Thread, Mutex]

global: Mutex

LazyWorker: class extends Runnable {
    run: func {
        // Wait around to aquire a lock on global...
        global acquire()

        "Got a lock, but I don't want to work, so releasing..." println()
        global release()
    }
}

SpoiledWorker: class extends Runnable {
    run: func {
        // It's all mine!
        global acquire()
    }
}

main: func {
    global = Mutex new()

    Thread new(LazyWorker new()) start()
    Thread new(LazyWorker new()) start()
    Thread new(LazyWorker new()) start()

    Thread new(SpoiledWorker new()) start()

    if(global tryAcquire()) {
        "Global is available!" println()
        global release()
    }
    else {
        "Global is not available :(" println()
    }
}
