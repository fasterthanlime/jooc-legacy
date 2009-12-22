#include <stdio.h>
#include <entrypoint/mylib.h>

int main() {
	
	printf("the app is starting!\n");
	mylib_init();
	printf("The answer is %d\n", addi(12, 30));

	return 0;

}
