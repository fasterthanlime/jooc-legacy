import text.PCRE, structs.ArrayList;

func main {
    String haystack = "Pincemi et Pincemoi sont dans un bateau";
    String pattern  = "Pince.{2,3}";
    printf("Let's go to find \"%s\" PCRE pattern into \"%s\" string\n", pattern, haystack);

    //new regexp
    PReg re = new ("Pince.{2,3}");

    //get matches
    ArrayList ar = re.match (haystack);
    for (String found: ar){
        printf ("Found: %s\n", found);
    }


    //:) 
    printf ("That's all folks!\n");
}
