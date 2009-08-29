
abstract class Vector {
	
	static const Float EPSILON = 0.000001f;
	
	abstract func squaredLength -> Float;

	abstract func length -> Float;
	
	abstract func reverse;
	
	abstract func normalize;
	
	abstract func repr -> String;

}
	
