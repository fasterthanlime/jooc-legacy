import structs/HashMap
import text/StringTemplate

main: func {
    template := "Hello {{name}}, happy {{   adjective    }} {{ day }}! Empty: '{{ does not exist }}'. Foo: '{{ foo bar }}'."
    values := HashMap<String> new()
    values put("foo bar", "Bar")
    values put("adjective", "rainy")
    values put("name", "KALAMAZOO")
    values put("day", "birthday")
    template formatTemplate(values) println()
}
