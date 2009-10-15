import structs/Stack

main: func {
	"Creating stack" println()
	stack := Stack<Int> new()
	printf("Size: %d\n", stack size())
		
	"Pushing 10 items on the stack" println()
	for (i in 0..10)
		stack push(i)
	
	while (!stack isEmpty())
		printf("peeked %d, popped %d\n", stack peek(), stack pop())		
}
