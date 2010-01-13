/**
 * Win32 type covers
 * 
 * @author Amos Wenger, aka nddrylliog
 */

version(windows) {
    
    include windows
    
    /*
     * File handle
     */
    Handle: cover from HANDLE
    INVALID_HANDLE_VALUE: extern Handle
    
    /*
     * Large integers
     */
    LargeInteger: cover from LARGE_INTEGER {
		lowPart : extern(LowPart)  Long
		highPart: extern(HighPart) Long
		quadPart: extern(QuadPart) LLong
	}
    
    /*
     * Unsigned large integers
     */
    ULargeInteger: cover from ULARGE_INTEGER {
		lowPart : extern(LowPart)  Long
		highPart: extern(HighPart) Long
		quadPart: extern(QuadPart) LLong
	}
    
}