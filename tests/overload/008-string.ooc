A: class {}

operator [] (a: A, index: Int) -> Int {
    return 1
}

main: func {
    s := "hello"
    ch := s[0]
	printf("should be h: %c\n", ch)
    a := A new()
    one := a[0]
	printf("should be 1: %d\n", one)
}
