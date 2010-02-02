import text/StringTokenizer

printParts: func (iter: Iterable<String>) {
    for(part: String in iter) {
        part println()
    }
    "" println()
}

main: func {
    s := "This/is/a/path/yes/it/is"
    for(i: Int in 0..7) {
        printParts(s split('/', i))
    }
}
