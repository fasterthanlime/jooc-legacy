EscapeSequence: class {
    valid := static 1
    needMore := static 2
    invalid := static 3

    /** This is a function for decoding an escape sequence. It supports
      * the most common escape sequences and also hexadecimal (\x0a) and
      * octal (\101) escape sequences.
      * You have to pass the escape sequence *without* the leading backslash
      * as `sequence` and a pointer to the result char as `chr`.
      * The return value is one of `EscapeSequence valid` (`chr` contains a
      * valid value now), `EscapeSequence needMore` (`chr`'s content is
      * undefined, the escape sequence is incomplete (the case for "\x1") and
      * `EscapeSequence invalid` (like for "\u").
      */
    getCharacter: static func (sequence: String, chr: Char*) -> Int {
        match(sequence[0]) {
            case '\'' => chr@ = '\''
            case '"' => chr@ = '"'
            case '\\' => chr@ = '\\'
            case '0' => chr@ = '\0'
//            case 'a' => chr@ = '\a' /* TODO: ooc doesn't know it */
            case 'b' => chr@ = '\b'
            case 'f' => chr@ = '\f'
            case 'n' => chr@ = '\n'
            case 'r' => chr@ = '\r'
            case 't' => chr@ = '\t'
            case 'v' => chr@ = '\v'
            case 'x' => {
                /* \xhh */
                if(sequence length() >= 3) {
                    /* have enough. convert heaxdecimal to `chr`. TODO: not nice */
                    sequence = sequence toUpper()
                    chr@ = 0
                    for(i in 0..2) {
                        value := 0
                        if(sequence[2-i] >= 'A' && sequence[2-i] <= 'F') {
                            value = 10 + sequence[2-i] - 'A'
                        } else if(sequence[2-i] >= '0' && sequence[2-i] <= '9') {
                            value = sequence[2-i] - '0'
                        } else {
                            /* invalid character in hexadecimal literal. */
                            return This invalid
                        }
                        chr@ += pow(16, i) * value
                    }
                    return This valid
                } else {
                    /* not enough characters. */
                    return This needMore
                }
            }
            case => {
                /* octal? */
                if(sequence[0] >= '0' && sequence[0] < '8') {
                    /* octal. */
                    chr@ = 0
                    octLength := sequence length() - 1
                    for(i in 0..octLength + 1) {
                        value := 0
                        if(sequence[octLength-i] >= '0' && sequence[octLength-i] < '8') {
                            value = sequence[octLength-i] - '0'
                        } else {
                            /* invalid character in octal literal. */
                            return This invalid
                        }
                        chr@ += pow(8, i) * value
                    }
                    return This valid
                }
                /* wtf. */
                return This invalid
            }
        }
        return This valid
    }
}
