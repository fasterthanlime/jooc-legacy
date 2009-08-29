import structs.List;
import structs.ArrayList;

func main(Int argc, String[] argv) {

	List list = new ArrayList;
	list.add("Once upon a midnight dreary, while I pondered weak and weary,");
	list.add("Over many a quaint and curious volume of forgotten lore,");
	list.add("While I nodded, nearly napping, suddenly there came a tapping,");
	list.add("As of some one gently rapping, rapping at my chamber door.");
	list.add("`'Tis some visitor,' I muttered, `tapping at my chamber door -");
	list.add("Only this, and nothing more.'");
	
	printf("The Raven - Edgar Allan Poe\n\n");
	for(String line: list) {
		printf("%s\n", line);
	}

}
