include stdio, math;

import math.Vector2f;

class Prey {
	
	static const Int PERCEPTION = 4;
	
}

class Animal {
	
	static const Float ENERGY_MOVE = 0.1f;
	
	Float x;
	Float y;
	Float lifeLevel;
	
	new() {
		x = 10f;
		y = 7f;
	}
	
}

func main {

	/*
	Vector2f v1 = new(1f, 0f);
	Vector2f v2 = new(0f, 1f);
	printf("We have vectors v1=%s, v2=%s\n", v1.repr, v2.repr);
	v2.mul(2f);
	printf("v2 = (2 * v2) = %s, length = %.2f\n", v2.repr, v2.length);
	v1.add(v2);
	printf("v1 = (v1 + v2) = %s, length = %.2f\n", v1.repr, v1.length);
	v1.normalize;
	printf("v1 normalized = %s, length = %.2f\n", v1.repr, v1.length);
	v2.scale(4f);
	printf("v2 scaled to 4 = %s, length = %.2f\n", v2.repr, v2.length);
	*/
	
	Animal animal = new;
	
	Vector2f vec = new;
		
	Int minX = animal.x - Prey.PERCEPTION;
	Int maxX = animal.x + Prey.PERCEPTION;
	Int minY = animal.y - Prey.PERCEPTION;
	Int maxY = animal.y + Prey.PERCEPTION;
	Int count = 0;
	for(Int x: minX..maxX) {
		for(Int y: minY..maxY) {
			Int numPredators = 4;
			for(Int i: 0..numPredators) {
				vec.add(2f, 4f);
				count++;
			}
		}
	}
	
	Vector2f direction = new;
	if(count == 0) {
		// No Predator to escape from? Just choose a random direction, then
		direction.set((rand() % 6) - 3f, (rand() % 6) - 3f);
	} else {
		// Heeeeeelp! we're being chased, just go far away.
		//direction.set(vec);
		direction.div((Float) count);
		direction.scale(sqrt(animal.lifeLevel / Animal.ENERGY_MOVE));
		direction.reverse();
	}
	
	printf("squaredLength = %.2f\n", direction.squaredLength);

}
