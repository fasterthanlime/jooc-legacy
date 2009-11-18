
main: func {
    
    block := [[1, 0, 0, 6],
              [0, 1, 0, 4],
              [0, 0, 1, 2]]
    
    block2: Int[3][3] = [[1, 0, 0],
                         [0, 1, 0],
                         [0, 0, 1]]
                         
    
    
    print(block as Int*, 3, 4)
    print(block2 as Int*, 3, 3)
    
}

print: func ~block(block: Int*, width, height: Int) {

    for(y in 0..height) {
        for(x in 0..width) {
            // works:
            i : Int = block[x * width + y]
            // doesn't work:
            //i : Int = block[x][y]
            "%d " format(i) println()
        }
        println()
    }
    
}

