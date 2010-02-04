import structs/[HashMap, ArrayList], text/StringBuffer
import ../Process
import native/win32/errors

version(windows) {
    
include windows

// extern functions
ZeroMemory: extern func (Pointer, SizeT)
CreateProcess: extern func (...) -> Bool

// covers
StartupInfo: cover from STARTUPINFO {
    structSize: extern(cb) Long
}
ProcessInformation: cover from PROCESS_INFORMATION

ProcessWin32: class extends Process {

    cmdLine: String = ""

    init: func ~win32 (=args) {
        sb := StringBuffer new()
        for(arg in args) {
            sb append('"'). append(arg). append("\" ")
        }
        cmdLine = sb toString()
    }
    
    /** Wait for the process to end. Bad things will happen if you haven't called `executeNoWait` before. */
    wait: func -> Int { 0 }
    
    /** Execute the process without waiting for it to end. You have to call `wait` manually. */
    executeNoWait: func {
        si: StartupInfo
        ZeroMemory(si&, StartupInfo size)
        si structSize = StartupInfo size
        
        pi: ProcessInformation
        ZeroMemory(pi&, ProcessInformation size)

        // Reference: http://msdn.microsoft.com/en-us/library/ms682512%28VS.85%29.aspx
        // Start the child process. 
        if(!CreateProcess(
            null,        // No module name (use command line)
            cmdLine,     // Command line
            null,        // Process handle not inheritable
            null,        // Thread handle not inheritable
            false,       // Set handle inheritance to false
            0,           // No creation flags
            null,        // Use parent's environment block
            null,        // Use parent's starting directory 
            si&,         // Pointer to STARTUPINFO structure
            pi&          // Pointer to PROCESS_INFORMATION structure
        )) {
            Exception new(This, "CreateProcess failed (%d).\n" format(GetLastError())) throw()
            return
        }   
    }
    
}

}