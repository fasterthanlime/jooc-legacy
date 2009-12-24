
Bubble: class <T> {

    tickle: func {
        
        data: T* = gc_malloc(T size)
        
        data[0] = this[0]
        
    }
    
}

operator []   <T> (b: Bubble<T>, i: Int)    -> Int { printf("bubble[%d]\n", i); 42 }
operator []=  <T> (b: Bubble<T>, i, j: Int) -> Int { printf("bubble[%d] = %d\n", i, j); 42 }

main: func {
    
    bubble := Bubble<Int> new()
    bubble tickle()
    
}