helloThere: mangled(yay) func {
    "Hi There!" println()
}

yay: extern func

main: func {
    helloThere()
    yay()
}
