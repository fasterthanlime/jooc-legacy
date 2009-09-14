include sys/mman

/* Constants */
PROT_EXEC: extern Int
PROT_WRITE: extern Int
PROT_READ: extern Int
PROT_NONE: extern Int

MAP_FIXED: extern Int
MAP_SHARED: extern Int
MAP_PRIVATE: extern Int
MAP_DENYWRITE: extern Int
MAP_EXECUTABLE: extern Int
MAP_NORESERVE: extern Int
MAP_LOCKED: extern Int
MAP_GROWSDOWN: extern Int
MAP_ANONYMOUS: extern Int
MAP_FILE: extern Int
MAP_32BIT: extern Int
MAP_POPULATE: extern Int
MAP_NONBLOCK: extern Int

/* Functions */
/*start: Pointer, length: Int, prot: Int, flags: Int, fd: Int, offset: Int */
mmap: extern func(Pointer,Int, Int, Int, Int, Int) -> Pointer
/* start: Pointer, length: Int */
munmap: extern func(Pointer, Int) -> Int
