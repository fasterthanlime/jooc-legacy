import threading/Mutex
include pthread

version(unix || apple) {

/** PThread Mutex Attribute cover **/
PThreadMutexAttr: cover from pthread_mutexattr_t
pthread_mutexattr_init: extern func(...) -> Int
pthread_mutexattr_destroy: extern func(...) -> Int
pthread_mutexattr_settype: extern func(...) -> Int
PTHREAD_MUTEX_RECURSIVE: extern Int
PTHREAD_MUTEX_NORMAL: extern Int

/** PThread Mutex cover **/
PThreadMutex: cover from pthread_mutex_t
pthread_mutex_init: extern func(...) -> Int
pthread_mutex_destroy: extern func(...) -> Int
pthread_mutex_lock: extern func(...) -> Int
pthread_mutex_trylock: extern func(...) -> Int
PTHREAD_EBUSY: extern(EBUSY) Int


UnixMutex: class extends Mutex {
    mutex: PThreadMutex

    init: func(recursive: Bool) {
        // Set mutex type: fast or recursive?
        attr: PThreadMutextAttr
        pthread_mutexattr_init(attr&)
        pthread_mutexattr_settype(attr&, match recursive {
            case true => PTHREAD_MUTEX_RECURSIVE
            case => PTHREAD_MUTEX_NORMAL
        })

        // Initialize our mutex
        pthread_mutex_init(mutex&, attr&)
    }

    __destroy__: func {
        pthread_mutex_destroy(mutex&)
    }

    acquire: func {
        if(pthread_mutex_lock(mutex&)) {
            Exception new("Error acquiring mutex lock") throw()
        }
    }

    tryAcquire: func -> Bool {
        result := pthread_mutex_trylock(mutex&)
        if(result == 0) return true
        else if(result == PTHREAD_EBUSY) return false
        else {
            Exception new("Error trying to acquire mutex lock") throw()
            return false
        }
    }

    release: func {
        if(pthread_mutex_unlock(mutex&)) {
            Exception new("Error unlocking mutex") throw()
        }
    }
}
