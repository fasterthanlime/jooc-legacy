import lang/types into Types

IntAddon: cover from Types Int extends Types Int {
    test: func {
        (this + 42) toString() println()
    }
}

main: func {
    0 as IntAddon test()
}
