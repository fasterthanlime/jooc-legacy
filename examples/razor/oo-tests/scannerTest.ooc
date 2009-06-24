#define GNU_SOURCE
include stdlib, stdio, string;

import text.StringTokenizer;
import io.Scanner;
import io.FileReader;
import io.StringReader;
import lang.String;

func main {

	printf("Reading from file:\n");
	printInts(new Scanner(new FileReader("reader.txt")));
	printf("Reading from string:\n");
	printInts(new Scanner(new StringReader("124 80 978\t208\n\n259")));

	printData;

}

func printInts(Scanner s) {

	s.skipWhitespace;
	while(s.hasNext) {
		Int i = s.readInt;
		printf("%d, ", i);
		s.skipWhitespace;
	}
	printf("\n");

}

func printData {

	printf("Now reading lines from a file\n");
	Scanner s = new Scanner(new FileReader("reader2.txt"));
	while(s.hasNext) {
		String line = s.readLine;
		if(line.isEmpty) {
			continue;
		}
		StringTokenizer tk = new StringTokenizer(line, "\t");
		String name = tk.nextToken, age = tk.nextToken, profession = tk.nextToken;
		printf("Name: %s, Age: %s, Profession: %s\n", name, age, profession);
	}

}
