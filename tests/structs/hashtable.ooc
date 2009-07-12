import structs.HashTable;

func main(Int argc, String[] argv) {
	HashTable ht = new(100);
	
	printf("Contains foo? %s\n", ht.contains("foo") ? "yes" : "no");
	
	printf("Adding foo -> bar\n");
	ht.put("foo", "bar");
	printf("Contains foo now? %s\n", ht.contains("foo") ? "yes" : "no");
	printf("%s\n", (String)ht.get("foo"));
	
	printf("Overwriting foo with baz\n");
	ht.put("foo", "baz");
	printf("%s\n", (String)ht.get("foo"));
	
	printf("Removing foo\n");
	ht.remove("foo");
	printf("Contains foo now? %s\n", ht.contains("foo") ? "yes" : "no");
}
