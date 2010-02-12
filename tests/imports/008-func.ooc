import io/File into IO

printName: func (file: IO File) {
    file name() println()
}

main: func {
    printName(IO File new("008-func.ooc"))
}
