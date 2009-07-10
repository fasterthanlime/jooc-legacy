import text.Regexp;

func main{

    String test = "Pincemi et Pincemoi sont dans un bateau";

    ArrayList matches = test.match("(Pince.{2,3})");
    
    for (String found: matches){
        printf("Found: %s\n", found);
    }

    //same with PCRE:
    Regexp.setEngine(Regexp.PCRE);

    matches = test.match("(Pince.{2,3})");
    
    for (String found: matches){
        printf("Found: %s\n", found);
    }



}
