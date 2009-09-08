include stdio;
String: cover from char*
extern func printf(fmt: String, ...)

func main {
	
	str := const "Hi, world!\n";
	printf(str);
	
}
