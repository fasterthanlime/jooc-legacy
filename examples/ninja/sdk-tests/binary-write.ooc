import io.FileWriter;
import io.FileReader;

func main {

	String fileName = "tchoutchou.txt";

	FileWriter fw = new FileWriter(fileName);
	Int i = 42;
	fw.write(&i, 0, sizeof(Int));
	fw.close;
	
	FileReader fr = new FileReader(fileName);
	Int j = 0;
	fr.read(&j, 0, sizeof(Int));
	printf("%d == %d. Alright\n", i, j);

}
