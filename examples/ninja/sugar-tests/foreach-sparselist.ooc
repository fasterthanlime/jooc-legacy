import structs.List;
import structs.SparseList;

func main(Int argc, String[] argv) {

	List list = new SparseList;
	list.add("Mais il remarqua avec sagesse :");
	list.add("- Les baobabs, avant de grandir, ça commence par être petit.");
	list.add("- C'est exact ! Mais pourquoi veux-tu que tes moutons mangent les petits baobabs ?");
	list.add("Cherchez l'intrus <>");
	list.add("Il me répondit : \"Ben ! Voyons !\" comme s'il s'agissait là d'une évidence.");
	list.add("Et il me fallut un grand effort d'intelligence pour comprendre à moi seul ce problème.");
	list.remove(3);
	
	printf("Le Petit Prince - Antoine de Saint-Exupéry\n\n");
	for(String line: list) {
		printf("%s\n", line);
	}

}









