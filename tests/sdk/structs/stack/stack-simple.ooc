import structs/Stack

main: func {
	"Creating stack" println()
	stack := Stack<Int> new()
	printf("Size: %d\n", stack size())
		
	"Pushing 10 items on the stack" println()
	for (i: Int in 0..10)
		stack push(i)
	
	printf("Size: %d\n", stack size())
	stack pop()
	printf("Size: %d\n", stack size())
	
}
