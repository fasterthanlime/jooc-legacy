use m; // libm is actually the math library
include math, stdio;

class Vector2f {

	static const Float EPSILON = 0.000001f;
	Float x;
	Float y;

	new() {
		x = 0;
		y = 0;
	}

	new(=x, =y);
	
	new(This copy) {
		x = copy.x;
		y = copy.y;
	}

	func squaredLength -> Float {
		(x * x + y * y);
	}

	func length -> Float {
		sqrt(x * x + y * y);
	}
	
	func reverse {
		x = -x;
		y = -y;
	}
	
	func normalize {
		Float _length = length();
		if(_length < 0) {
			if(_length > -EPSILON) {
				return;
			}
		} else {
			if(_length < EPSILON) {
				return;
			}
		}
		x /= _length;
		y /= _length;
	}
	
	func scale(Float newLength) {
		Float _length = length();
		if(_length < 0) {
			if(_length > -EPSILON) {
				return;
			}
		} else {
			if(_length < EPSILON) {
				return;
			}
		}
		x /= _length / newLength;
		y /= _length / newLength;
	}
	
	func set(=x, =y);
	
	func set(This t) {
		x = t.x;
		y = t.y;
	}
	
	func add(This t) {
		x += t.x;
		y += t.y;
	}
	
	func sub(This t) {
		x -= t.x;
		y -= t.y;
	}
	
	func mul(Float f) {
		x *= f;
		y *= f;
	}
	
	func div(Float f) {
		x /= f;
		y /= f;
	}
	
	func add(Float x, Float y) {
		this.x += x;
		this.y += y;
	}
	
	func add(Int x, Int y) {
		this.x += x;
		this.y += y;
	}
	
	func sub(Float x, Float y) {
		this.x -= x;
		this.y -= y;
	}
	
	func sub(Int x, Int y) {
		this.x -= x;
		this.y -= y;
	}
	
	func repr -> String {
		const Int BUFFER_SIZE = 50;
		String buffer = malloc(BUFFER_SIZE);
		snprintf(buffer, BUFFER_SIZE, "(%.3f, %.3f)", x, y);
		return buffer;
	}

}
