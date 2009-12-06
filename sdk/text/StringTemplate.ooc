import text/StringBuffer
import structs/HashMap

String: cover from Char* {
    formatTemplate: func (values: HashMap<String>) -> String {
        length := this length()
        buffer := StringBuffer new(length)
        p: Char* = this
        identifier: Char* = null
        while(p@) {
            if(!identifier && p@ == '{' && (p + 1)@ == '{') {
                /* start of an identifier */
                identifier = p + 2
                p += 2
            } else if(identifier) {
                if(p@ == '}' && (p + 1)@ == '}') {
                    /* end of an identifier! */
                    length := (p - identifier) as SizeT
                    key := String new(length)
                    memcpy(key, identifier, length)
                    /* (the \0 byte is already set.) */
                    value := values get(key)
                    if(!value) {
                        value = "" /* TODO: better error handling. */
                    }
                    buffer append(value)
                    identifier = null
                    p += 2
                } else {
                    /* part of the identifier, skip */
                    p += 1
                }
            } else {
                buffer append(p@)
                p += 1
            }
        }
        return buffer toString()
    }
}
