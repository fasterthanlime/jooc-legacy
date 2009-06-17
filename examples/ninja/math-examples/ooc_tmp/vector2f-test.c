/*
 * Generated by ooc, the Object-Oriented C compiler, by Amos Wenger, 2009
 */

// OOC dependencies
#include "vector2f-test.h"

/*
 * Definition of class Prey
 */

Int Prey_PERCEPTION = 4;

Prey__class Prey__classInstance;


struct Prey*  __Prey_new() {

	Prey this = GC_malloc(sizeof(struct Prey));

	if(Prey__classInstance == NULL) {
		Prey__classInstance = GC_malloc(sizeof(struct Prey__class));
		Prey__classInstance ->name = "Prey";
	}
	this->class = Prey__classInstance;

	

	return this;


}
/*
 * Definition of class Animal
 */

Float Animal_ENERGY_MOVE = 0.1f;

Animal__class Animal__classInstance;


struct Animal*  __Animal_new() {

	Animal this = GC_malloc(sizeof(struct Animal));

	if(Animal__classInstance == NULL) {
		Animal__classInstance = GC_malloc(sizeof(struct Animal__class));
		Animal__classInstance ->name = "Animal";
	}
	this->class = Animal__classInstance;

	
	this->x = 10.0f;
	this->y = 7.0f;

	return this;


}

Int main() {

	GC_init();	

	/*
	 * Vector2f v1 = new(1f, 0f);
	 * Vector2f v2 = new(0f, 1f);
	 * printf("We have vectors v1=%s, v2=%s\n", v1.repr, v2.repr);
	 * v2.mul(2f);
	 * printf("v2 = (2 * v2) = %s, length = %.2f\n", v2.repr, v2.length);
	 * v1.add(v2);
	 * printf("v1 = (v1 + v2) = %s, length = %.2f\n", v1.repr, v1.length);
	 * v1.normalize;
	 * printf("v1 normalized = %s, length = %.2f\n", v1.repr, v1.length);
	 * v2.scale(4f);
	 * printf("v2 scaled to 4 = %s, length = %.2f\n", v2.repr, v2.length);
	 */
	
	struct Animal*  animal = __Animal_new();
	struct math_Vector2f*  vec = __math_Vector2f_new();
	Int minX = animal->x  - Prey_PERCEPTION;
	Int maxX = animal->x  +  Prey_PERCEPTION;
	Int minY = animal->y  - Prey_PERCEPTION;
	Int maxY = animal->y  +  Prey_PERCEPTION;
	Int count = 0;
	for(int x = minX; x < maxX; x += 1)  {
		for(int y = minY; y < maxY; y += 1)  {
			Int numPredators = 4;
			for(int i = 0; i < numPredators; i += 1)  {
				vec->class->__add_Float_Float(vec, 2.0f, 4.0f);
				count ++;
			}
		}
	}
	struct math_Vector2f*  direction = __math_Vector2f_new();
	if(count == 0) {
		//  No Predator to escape from? Just choose a random direction, then 

		direction->class->__set_Float_Float(direction, (rand() % 6) -3.0f,(rand() % 6) -3.0f);
	}
	else {
		//  Heeeeeelp! we're being chased, just go far away. 

		// direction.set(vec); 

		direction->class->__div_Float(direction, (Float) count);
		direction->class->__scale_Float(direction, sqrt(animal->lifeLevel / Animal_ENERGY_MOVE));
		direction->class->__reverse(direction);
	}
	printf("squaredLength = %.2f\n", direction->class->__squaredLength(direction));


}
