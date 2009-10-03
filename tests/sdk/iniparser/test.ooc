import iniparser/iniparser

main: func {

    file := "twisted.ini"
    d: DictPtr
    d = iniparser_load(file)
    iniparser_getstring(d, "quotes:h1", "blub") println()

}
