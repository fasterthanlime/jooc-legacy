someVeryLongVariable: mangled(shortName) Int
shortName: extern Int

main: func {
    shortName = 456
    "455 + 1 = %d" format(shortName) println()
}
