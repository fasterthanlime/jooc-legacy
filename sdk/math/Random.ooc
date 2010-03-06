import os/Time
import structs/List

__STATE := Time microtime()
srand(__STATE)

Random: class {
    state = __STATE :static Long

    random: static func -> Int {
        return rand()
    }

    randInt: static func(start, end: Int) -> Int {
        return randRange(start, end+1)
    }
    
    randRange: static func(start, end: Int) -> Int {
        width := end - start
        return start + (random() % width)
    }

    choice: static func <T> (l: List<T>) -> T {
        return l get(randRange(0, l size()))
    }
} 





