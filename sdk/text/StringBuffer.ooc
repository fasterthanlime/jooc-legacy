StringBuffer: class {
    
    size: SizeT
    capacity: SizeT
    data: String
    
    init: func {
        this(128)
    }
    
    init: func ~withCapa (=capacity) {
        data = gc_malloc(capacity)
        size = 0
    }
    
    append: func ~str (str: String) {
        length := str length()
        append(str, length)
    }

    append: func ~strWithLength (str: String, length: SizeT) {
        checkLength(size + length)
        memcpy(data as Char* + size, str, length)
        size += length
    }
    
    append: func ~chr (chr: Char) {
        checkLength(size + 1)
        data[size] = chr
        size += 1
    }
    
    checkLength: func (min: SizeT) {
        if(min >= capacity) {
            newCapa := min * 1.2 + 10
            tmp := gc_realloc(data, newCapa)
            if(!tmp) {
                Exception new(This, "Couldn't allocate enough memory for StringBuffer to grow to capacity "+newCapa) throw()
            }
            data = tmp
            capacity = newCapa
        }
    }
    
    toString: func -> String {
        if(size + 1 < capacity) {
            newCapa := size + 1
            tmp := gc_realloc(data as Char*, newCapa)
            if(!tmp) Exception new(This, "Not enough memory to reallocate string in buffer for toString() call") throw()
            data = tmp
            capacity = newCapa
        }
        data[size] = '\0'
        return data // ugly hack. or is it?
    }
    
}
