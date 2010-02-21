/*
 * Example micro-benchmark for totally unsafe memory allocation
 * vs errorcode-checked memory allocation. 
 * 
 * It's C99 and POSIX.1-2001 (for gettimeofday), tested under Gentoo/GCC 4.3.4
 * 
 * Compile it with: 
 * 	 gcc -std=c99 errcheck.c -o errcheck
 * 
 * I plan to use this method (automatically generated, of course)
 * to handle exceptions in ooc.
 * Since everyone in serious game development since to avoid exceptions
 * for performance reasons, this should solve this particular problem.
 * 
 * Implementation notes:
 *   - Each function must specify its exceptions thrown explicitly (a-la Java)
 *   - To show stack traces, use libunwind: http://www.nongnu.org/libunwind/
 * 
 * So far, this solution is the most lightweight and simplest one I've found.
 * (sig)setjmp/longjmp are non-threadsafe, slow, not too portable
 * 
 * Amos Wenger, 2009
 * 
 * References:
 *   - http://www.freetype.org/david/reliable-c.html
 *   - http://www.gamearchitect.net/Articles/ExceptionsAndErrorCodes.html
 */

include stdlib, stdio, errno, string, sys/time

malloc: extern func (Int) -> Pointer
exit: extern func (Int)
errno: extern Int

/*************** Exceptions structures *******************/

ReturnValue: class {
	exception: Exception
	value: Pointer
	
	init: func (=exception, =value) {}
}

Exception: class {
	name: String
	message: String
	
	init: func (=name, =message) {}
}

/*************** Two malloc flavors *******************/

strerror: extern func (Int) -> String

safe_malloc: func (size: SizeT) -> ReturnValue {
	
	mem = malloc(size) : Pointer
	if(!mem) {
		return ReturnValue new(Exception new("OutOfMemoryException", strerror(errno)), null)
	}
	
	return ReturnValue new(null, mem)
	
}

try_alloc_safe: func (size: SizeT) {
	
	block_value = safe_malloc(size) : ReturnValue
	
	if(block_value exception) {
		printf("%s exception caught: %s\n",
				block_value exception name,
				block_value exception message)
		exit(1)
	}
	// } // end catch
	block = block_value value : String
	// } // end try
	
	//free(block)
	
}

try_alloc_unsafe: func (size: SizeT) {
	
	block = malloc(size) : String
	//free(block)
	
}

/*************** Test code *******************/

MAX_ITER := const 10000000

//ALLOC_SIZE = 10000: const Int
// this makes us go out of memory (for testing exception catching)
ALLOC_SIZE := const 3000000000

main: func -> Int {
	
	for(i in 0..MAX_ITER) {
		try_alloc_safe(ALLOC_SIZE)
	}
	
	for(i in 0..MAX_ITER) {
		try_alloc_unsafe(ALLOC_SIZE)
	}
	
	return 0
	
}
