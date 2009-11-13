main: func {

    "Enter a name" println()
    name1 := stdin readLine()
    "Now enter another name" println()
    name2 := stdin readLine()
    
    match {
        case (name1 == name2)
            => "They're equal =)"
        case
            => "They have their own individuality"
    } println()

}
