#define GNU_SOURCE
include stdlib, stdio, string;

import text.StringTokenizer;
import io.Scanner;
import io.FileReader;
import io.StringReader;

func reverse(String s) -> String {
	
	Char c;
	String p, q;
	if (s!=NULL && *s!=0) { // No empty strings, please! 
		q = s;
		while (*(++q)) ; // points q at '0' terminator;
		for (p=s; p < --q; p++) { // ignores middle character when strlen is odd
			c = *p;
			*p = *q;
			*q = c;
		}
	}
	
	return s;
	
}

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
		printf("Read integer value = %d, + 42 = %d\n", i, i + 42);
		s.skipWhitespace;
	}

}

func printData {

	printf("Now reading lines from a file\n");
	Scanner s = new Scanner(new FileReader("reader2.txt"));
	while(s.hasNext) {
		String line = s.readLine;
		reverse(line);
		if(strlen(line) == 0) {
			continue;
		}
		StringTokenizer tk = new StringTokenizer(line, "\t");
		//FIXME: Tokens are reversed, since C calls the functions from right to left, as opposed to Java. This should be fixed in the compiler.
		printf("Name: %s, Age: %s, Profession: %s\n", reverse(tk.nextToken()), reverse(tk.nextToken()), reverse(tk.nextToken()));
		//printf("Name: %s, Age: %s, Profession: %s\n", tk.nextToken(), tk.nextToken(), tk.nextToken());
	}

}
