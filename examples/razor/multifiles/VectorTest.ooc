#include <stdio.h>
#include <stdlib.h>

import math.Vector3d;
use SDL;

class VectorTest {
	new() {
		Vector3d v1 = new Vector3d(1.0, 2.0, 3.0);
		printf("Vector 1 : ");
		v1.print();

		Vector3d v2 = new Vector3d(4.0, -6.0, 9.0);
		printf("Vector 2 : ");
		v2.print();

		Vector3d v3 = new Vector3d(v1);
		v3.add(v2);
		printf("Vector 1 + 2 : ");
		v3.print();

		printf("Vector 1 . 2 : %f\n", v1.scalar(v2));
	}
}

int main() {
	new VectorTest();
	return 0;
}
