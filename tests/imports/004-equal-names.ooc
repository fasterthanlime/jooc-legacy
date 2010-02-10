import io/[File, FileWriter] into File
import io/FileReader into File

main: func {
    f := File File new("test.txt")
    reader := File FileReader new(f)
    contents := reader readLine()
    reader close()
    contents = contents append(" yay")
    writer := File FileWriter new(f)
    writer write(contents)
    writer close()
}
