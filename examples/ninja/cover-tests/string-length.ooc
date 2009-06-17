include stdio;
import lang.String;

func main {

	String s = "le chat est sur la branche =)";
	printf("length of '%s' = %d. starts with 'le' %d ? starts with 'la' %d ?\n", s, s.length, s.startsWith("le"), s.startsWith("la"));
	printf("first index of 'a': %d. last index of 'l': %d\n", s.indexOf('a'), s.lastIndexOf('l'));

}
