import threading/native/[MutexUnix, MutexWin32]

/**
    A synchronization object that allows mutal exclusion
    of a shared resource via locking and unlocking.
*/
Mutex: abstract class {
    /**
        Create a new mutex object.

        :recursive: if true this mutex will allow for recursive locking in the same thread
    */
    new: static func(recursive: Bool) -> This {
        version (unix || apple) {
          return MutexUnix new(recursive)
        }
        /*version (windows) {
          return MutexWin32 new(recursive)
        }*/

        Exception new(This, "Unsupported platform!\n") throw()
        null
    }

    /**
        Create a new mutex object. Non-recursive, fast.
    */
    new: static func ~fast -> This { This new(false) }

    /**
        Acquire a lock on the mutex and blocking until it becomes available.
    */
    acquire: abstract func

    /**
        Try to acquire a lock on the mutex, but does not block if unavailable.

        :return: true if mutex successfully locked, false if not available
    */
    tryAcquire: abstract func -> Bool

    /**
        Release the lock on the mutex so other threads can acquire it.
    */
    release: abstract func
}
