import text.Regexp;

func main {
	String text = "Pincemi et Pincemoi sont dans un bateau...";
	Regexp r=new ("(Pince.{2,3})"); 
	
	ArrayList ret;
	ret = r.match(text); //trouver Pincemi ou Picemoi
	
	for (String match: ret){
	        printf ("found: %s\n", match);
	}

	
}
