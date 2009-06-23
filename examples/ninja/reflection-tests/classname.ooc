import structs.Array;

func main {

	KoolAid kl = new;
	printf("Everybody needs some %s sometimes.\n", kl.class.name);

}

class KoolAid {

	Array a;

	new() {
		a = new(1);
		printf("Who needs some %s? Or a %s?\n", this.class.name, a.class.name);
		printf("No, really, I've got some %s left. You sure?\n", class.name);
	}

}
