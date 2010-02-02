import text/StringTokenizer

main: func {

    st := StringTokenizer new("This    is a  test, dude | Sorry for not being polite", " ,|")

    /*
    for (s in st) {
        s println()
    }
    */

    iter := st iterator()

    while (iter hasNext()) {
        s := iter next()
        s println()
    }

}
