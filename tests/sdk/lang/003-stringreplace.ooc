main: func {
    test := "Hello World!"
    test println()
    "Count of 'e': %d" format(test count('e')) println()
    "Count of 'o': %d" format(test count('o')) println()
    "Count of 'x': %d" format(test count('x')) println()
    "Hello -> Goodbye: %s" format(test replace("Hello", "Goodbye")) println()
    test = "LOLOLOLO"
    test println()
    "Count of \"LOL\": %d" format(test count("LOL")) println()
    "Count of \"LO\": %d" format(test count("LO")) println()
    "LOL -> FUU: %s" format(test replace("LOL", "FUU")) println()
}
