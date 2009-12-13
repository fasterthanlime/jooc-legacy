import structs/[ArrayList, Stack]
import text/StringTokenizer

main: func {
    tokenizer := "a.b.c" split('.')
    for(s: String in tokenizer toArrayList()) {
        s println()
    }
    stack := Stack<Int> new()
    stack push(4) .push(5)
    for(i: Int in stack toArrayList()) {
        i toString() println()
    }
}
