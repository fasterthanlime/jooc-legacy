import text/Shlex
import structs/ArrayList

printShlexed: func (s: String) {
    "'%s' => " format(s) print()
    for(part in Shlex split(s)) {
        "'%s' " format(part) print()
    }
    printf("\n")
}

main: func {
    printShlexed("one two --three four five")
    printShlexed("hello-hello-hello \"Oh my god I am quoted!\" bye-bye-bye")
    printShlexed(" hel     'Single Quotes for the win!' lo")
    printShlexed("'And here' \"we've got\" 'three quoted \\'values\\'.'")
    printShlexed("I want to try \"escape sequences\": \"\\101\\n\\x47\"")
}
