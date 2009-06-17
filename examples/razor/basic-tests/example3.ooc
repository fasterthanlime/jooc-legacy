include stdio, stdlib;

class Pizza {

	String name;
	Double radius;
	String color;
	Double tomatoQuantity;

	new(=name, =radius, =color, =tomatoQuantity);

	func describe {
		printf("Pizza %s, of diameter %f, color %s, and tomato percentage %f\n", name, radius * 2, color, tomatoQuantity * 100);
	}

}

abstract class PizzaFactory {

	abstract func makePizza -> Pizza;

}

class ChezGino from PizzaFactory {

	implement makePizza {
		new Pizza("Allegro Tomato", 0.25, "red", 0.6);
	}

}

class PizzaMaite from PizzaFactory {

	implement makePizza {
		new Pizza("Royale Grande", 0.50, "green", 0.4);
	}

}

func main {
	
	PizzaFactory[] factories = {new ChezGino, new PizzaMaite};
	
	//for(PizzaFactory factory: factories) {
	for(Int i: 0..2) {
		factories[i].makePizza.describe;	
	}

}
