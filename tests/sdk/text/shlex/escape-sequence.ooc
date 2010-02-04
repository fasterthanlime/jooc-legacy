import text/EscapeSequence

printSeq: func (seq: String) {
    chr := '\0'
    ret := EscapeSequence getCharacter(seq as Char* + 1, chr&)
    if(ret == EscapeSequence valid) {
        "%s => %c (%d)" format(seq, chr, chr) println()
    } else if(ret == EscapeSequence invalid) {
        "%s is invalid" format(seq) println()
    } else if(ret == EscapeSequence needMore) {
        "%s needs more" format(seq) println()
    }
}

main: func {
    printSeq("\\t")
    printSeq("\\n")
    printSeq("\\a")
    printSeq("\\x0a")
    printSeq("\\101")
    printSeq("\\x7")
    printSeq("\\x")
    printSeq("\\\\")
    printSeq("\\u")
    printSeq("\\1")

    unescaped := EscapeSequence unescape("Hello\\tsir.\\nThis is an \\101: \\x41. \x95. Backslash: \\\\. \\unknown")
    unescaped println()
    EscapeSequence escape(unescaped) println()
}
