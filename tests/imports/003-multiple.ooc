import io/[File, FileWriter] into F
import io/FileReader into F

main: func {
    f := F File new("test.txt")
    reader := F FileReader new(f)
    contents := reader readLine()
    reader close()
    contents = contents append(" yay")
    writer := F FileWriter new(f)
    writer write(contents)
    writer close()
}
