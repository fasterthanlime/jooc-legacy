import os/Time
import structs/[ArrayList,List]

__STATE := Time microtime()
srand(__STATE)

Random: class {
    state = __STATE  :static Long
    
    random: static func -> Int {
        return rand()
    }

    randInt: static func(start, end: Int) -> Int {
        return randRange(start, end+1)
    }
    
    randInt: static func ~exclude(start, end: Int, ex: List<Int>) -> Int {
        return exclude(start, end, ex, randInt as Func)
    }

    randRange: static func(start, end: Int) -> Int {
        width := end - start
        return start + (random() % width)
    }

    randRange: static func ~exclude(start, end: Int, ex: List<Int>) -> Int {
        return exclude(start, end, ex, randRange as Func)
    }
    
    choice: static func <T> (l: List<T>) -> T {
        return l get(randRange(0, l size()))
    }

    exclude: static func(start, end: Int, ex: List<Int>, f: Func (Int, Int) -> Int) -> Int {
        toRet := f(start, end)
        while (ex contains(toRet)) {
            toRet = f(start, end)
        }
        return toRet
    }
} 





