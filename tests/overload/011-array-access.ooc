
Bubble: class {
    
}

operator []  (b: Bubble, i: Int)    -> Int { printf("bubble[%d]\n", i); 42 }
operator []= (b: Bubble, i, j: Int) -> Int { printf("bubble[%d] = %d\n", i, j); 42 }

main: func {
    
    bubble := Bubble new()
    number : Int
    array : Int[255]
    
    // using and assigning the bubble with constants
    bubble[1]
    bubble[2] = 3
    
    // assigning from the bubble
    number   = bubble[1]
    array[1] = bubble[1]
    
    // assigning to the bubble
    bubble[1] = number
    bubble[1] = array[1]
    
}