include stdio;
cover String from char*;
extern func printf(String fmt, ...);

func main {
	
	String str = "Hi, world!\n";
	printf str;
	
}
