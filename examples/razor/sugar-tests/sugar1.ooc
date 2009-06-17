/**
 * Showcase ooc's syntaxic sugar
 * @author Amos Wenger
 */
class Sugar {
	
	static int accessCount;
	
	int quantity;
	String origin;
	
	/////////////////////////////////////////////////////////
	
	new(=*);
	
	
	new(int quantity, String origin) {
		this.quantity = quantity;
		this.origin = origin;
	}
	
	/////////////////////////////////////////////////////////
	
	new(=origin, =quantity);
	
	
	new(String origin, int quantity) {
		this.quantity = quantity;
		this.origin = origin;
	}
	
	/////////////////////////////////////////////////////////
	
	new(This t) {
		quantity = 3 * t.quantity;
	}
	
	
	new(Sugar s) {
		this.quantity = 3 * s.quantity;
	}
	
	/////////////////////////////////////////////////////////
	
	func get3 = int {
		3!
	}
	
	int get3() {
		return 3;
	}
	
	/////////////////////////////////////////////////////////
	
	func =quantity {
		printf "Hi there =)";
		printf <= ("I", "think", "you're", "beautiful");
		3 * quantity !
	}
	
	void quantity(int quantity) {
		printf("Hi there");
		printf("I");
		printf("think");
		printf("you're");
		printf("beautiful");
		this.quantity = 3 * quantity;
	}
	
	/////////////////////////////////////////////////////////
	
	func =quantity(q) {
		q - 1 !
	}
	
	void quantity(int q) {
		this.quantity = q - 1;
	}
	
	/////////////////////////////////////////////////////////
	
	func quantity {
		accessCount++;
		quantity !
	}
	
	int quantity() {
		Sugar.accessCount++;
		return this.quantity;
	}
	
	/////////////////////////////////////////////////////////
	
	func giggle = Laugh {
		new("Hehehehhehehehehe") !
	}
	
	Laugh giggle() {
		return new Laugh("hehehehehehehe");
	}
	
	/////////////////////////////////////////////////////////
	
	func specialize = This {
		new SpecialThis(this) !
	}
	
	Sugar specialize() {
		return new SpecialThis(this);
	}
	
	/////////////////////////////////////////////////////////
	
}

func main {
	
	/////////////////////////////////////////////////////////
	
	Sugar s = new(3, "France");
	
	Sugar s = new Sugar(3, "France");
	
	/////////////////////////////////////////////////////////

	s.origin = "Blah";
	
	s.origin = "Blah"; // Oh, really ? ;)
	
	/////////////////////////////////////////////////////////
	
	s.quantity = 3;
	
	s.quantity(3); // Notice the difference
	
	/////////////////////////////////////////////////////////
	
	printf s.quantity;
	
	printf(s.quantity());
	
	/////////////////////////////////////////////////////////
	
	with s { // s is a variable
		quantity = 3 * origin[3];
		with specialize { // specialize() is a method from quantity
			giggle; // called on the specialized version
		}
	}
	
	s.quantity(3 * s.origin[3]);
	Sugar s2 = s.specialize();
	s2.giggle();
	
	/////////////////////////////////////////////////////////
	
}

class Honda from Car, KoolAid, Jokari { // Shorter than 'extends'
	
	/*
	 * Implementation notes for interfaces:
	 * 
	 * As for classes, a struct for the class structure, and one for the
	 * object structure
	 * 
	 * In object;
	 * 
	 * struct ObjectStructureForRequestedInterface* getInterface(struct ClassStructureForRequestedInterface st) {
	 * 	
	 * }
	 */
	
}
