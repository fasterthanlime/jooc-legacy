Derived: cover from String extends String {}

main: func {
    d := "" as Derived
    c := d[0]
    c isDigit() /* to check if `c` is `Char` */
}
