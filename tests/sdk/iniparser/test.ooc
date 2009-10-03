import iniparser/INI

main: func {

    file := "twisted.ini"
    a := INI new(file)
    a getString("h1", "blub")
    a setCurrentSection("quotes")
    b := a getString("h1", "blub")
    b println()

}
