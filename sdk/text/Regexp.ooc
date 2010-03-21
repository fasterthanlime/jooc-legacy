use pcre

/**
    Low level PCRE cover used internally by Regexp
*/
Pcre: cover from pcre* {
    compile: extern(pcre_compile) static func(...) -> This
    free: extern(pcre_free) func
    exec: extern(pcre_exec) func(...) -> Int
    getStringNumber: extern(pcre_get_stringnumber) func(...) -> Int
}

/**
    Regular expression object
*/
Regexp: class {
    errorMsg: static String
    errorOffset: static Int

    pcre: Pcre
    maxSubstrings: Int = 30

    /**
        Compile a regular expression pattern.

        :param pattern: regular expression pattern to compile
        :param options: compiling options.
        :return: new regular expression object if successful, null if error occured.
    */
    compile: static func ~withOptions(pattern: String, options: Int) -> This {
        p := Pcre compile(pattern, options, errorMsg& as const Char**, errorOffset&, null)
        if(!p) {
            //TODO: once true exceptions work, throw an exception instead
            return null
        }
        return new(p)
    }
    compile: static func(pattern: String) -> This { compile(pattern, 0) }

    init: func(=pcre) {}

    __destroy__: func {
        pcre free()
    }

    /**
        If one or more characters from the start of the subject string
        matches the pattern, returns a Match object. Returns null if match fails.

        :param subject: subject string to test for match
        :param start: offset within subject at which to start matching
        :return: Match object if a match was found, otherwise null
    */
    matches: func ~withLengthAndStart(subject: String, start: Int, length: SizeT) -> Match {
        ovector: Int* = gc_malloc(sizeof(Int) * maxSubstrings)
        count := pcre exec(null, subject, length, start, 0, ovector, maxSubstrings)
        if(count > 0) {
            return Match new(this, count, subject, ovector)
        }
        else return null
    }
    matches: func(subject: String) -> Match { matches(subject, 0, subject length()) }
}

Match: class {
    regexp: Regexp
    substringCount: Int
    subject: String
    ovector: Int*

    init: func(=regexp, =substringCount, =subject, =ovector) {}

    substring: func ~byIndex(index: Int) -> String {
        if(index >= substringCount) {
            Exception new("Invalid substring index: %d" format(index)) throw()
        }

        offset := index * 2
        return subject substring((ovector + offset)@, (ovector + offset + 1)@)
    }

    substring: func ~byName(name: String) -> String {
        number := regexp pcre getStringNumber(name)
        if(number < -1) Exception new("Invalid substring name: %s" format(name)) throw()
        return substring(number)
    }
}
