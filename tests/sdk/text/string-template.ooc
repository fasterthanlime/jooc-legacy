import structs/HashMap
import text/StringTemplate

main: func {
    template := "Hello {{name}}, happy {{day}}!"
    values := HashMap<String> new()
    values put("name", "KALAMAZOO")
    values put("day", "birthday")
    template formatTemplate(values) println()
}
