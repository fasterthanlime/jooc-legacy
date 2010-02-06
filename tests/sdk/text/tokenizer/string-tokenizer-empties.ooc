import text/StringTokenizer

main: func {
    for(part in "One.two..." split('.', true))
        "'%s' " format(part) print()
}
