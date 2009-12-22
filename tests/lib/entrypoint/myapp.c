#include <stdio.h>
#include <entrypoint/mylib.h>

int main() {

	mylib_init();
	printf("The answer is %d\n", addi(13, 31));

	return 0;

}
