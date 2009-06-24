#include <stdio.h>
#include <stdlib.h>

/**
 * A Vector of three doubles.
 */
class Vector3d {
	double x;
	double y;
	double z;

	new(=x, =y, =z);

	new(Vector3d v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	void print() {
		printf("(%f, %f, %f)\n", x, y, z);
	}

	void add(Vector3d v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	double scalar(Vector3d v) {
		return (x * v.x) + (y * v.y) + (z * v.z);
	}
}
