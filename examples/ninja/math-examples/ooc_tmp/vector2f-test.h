#ifndef vector2f_test_h
#define vector2f_test_h

struct Prey;

struct Animal;

#include <stdio.h>
#include <math.h>
#include "math/Vector2f.h"
#include "OocLib.h"

typedef struct Prey__class {
	String name;
	
}* Prey__class;


typedef struct Prey {
	Prey__class class;
	
}* Prey;


extern Int Prey_PERCEPTION;
extern Prey__class Prey__classInstance;
extern Prey __Prey_new();


typedef struct Animal__class {
	String name;
	
}* Animal__class;



typedef struct Animal {
	Animal__class class;
	Float x;
	Float y;
	Float lifeLevel;
	
}* Animal;


extern Float Animal_ENERGY_MOVE;
extern Animal__class Animal__classInstance;
extern Animal __Animal_new();
Int main();


#endif // vector2f_test_h
