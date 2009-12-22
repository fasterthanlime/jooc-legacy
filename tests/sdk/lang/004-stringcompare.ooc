compare: func (this, other: String, start, length: SizeT) {
    value := this compare(other, start, length)
    "compare(\"%s\", \"%s\", %d, %d) -> %s" format(this, other, start, length, value ? "true" : "false") println()
}

main: func {
    compare("Hellow", "Hellouuuu", 0, 5)
    compare("Hellow", "Hellouuuu", 0, 6)
    compare("www.blabla.de", "blablaurghs", 4, 6)
    compare("www.blabla.de", "blablaurghs", 4, 7)
}
