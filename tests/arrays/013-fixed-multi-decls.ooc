
main: func {
    
    ab, ac, ap: Int[3]
    
    for(i in 0..3) {
        ab[i] = i
        ac[i] = i
        ap[i] = i
    }
    
    print(ab)
    print(ac)
    print(ap)
    
}

print: func (a: Int*) {
    printf("[%d, %d, %d]\n", a[0], a[1], a[2])
}
