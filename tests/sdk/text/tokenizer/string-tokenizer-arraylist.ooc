import text/StringTokenizer
import structs/ArrayList

main: func {
    parts := "Hello World and Goodbye!" split(' ') toArrayList()
    for(s: String in parts) {
        s println()
    }
}
