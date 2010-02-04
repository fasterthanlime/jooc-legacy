
import ../Process

ProcessWin32: class extends Process {

    init: func ~win32 (=args) {}
    
    /** Wait for the process to end. Bad things will happen if you haven't called `executeNoWait` before. */
    wait: func -> Int { 0 }
    
    /** Execute the process without waiting for it to end. You have to call `wait` manually. */
    executeNoWait: func {}
    
}