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
        version (windows) {
          return MutexWin32 new(recursive)
        }

        Exception new(This, "Unsupported platform!\n") throw()
        null
    }

    /**
        Create a new mutex object. Non-recursive, fast.
    */
    new: static func ~normal -> This { This new(false) }

    /**
        Lock the mutex and block until it becomes available.

        :timeout: maxium number of milliseconds to block
    */
    lock: abstract func ~withTimeout(timeout: Int)

    /**
        Lock the mutex and block indefinitely until it becomes available.
    */
    lock: abstract func

    /**
        Attempt to lock the mutex, but do not block if it is unavailable.

        :return: true if mutex successfully locked, false if not available
    */
    tryLock: abstract func -> Bool

    /**
        Release the lock on the mutex so other threads can access it.
    */
    unlock: abstract func
}
