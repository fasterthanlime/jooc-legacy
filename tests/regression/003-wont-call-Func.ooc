foobar: func { "Hey, foobar called =)" println() }

getFoobar: func -> Func {
    foobar
}

callFoobar: func {
    privateFoobar := getFoobar()
    privateFoobar()
}

callFoobar()
