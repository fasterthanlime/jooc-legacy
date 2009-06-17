/**
 * This program computes prime numbers using the Sieve of Eratosthenes
 * algorithm: rule out multiples of all lower prime numbers, and anything
 * remaining is a prime. It prints out the largest prime number less than or
 * equal to the supplied command-line argument.
 */
func main (String[] args) {
	
	int max = (args[0] as int) or 100;
	
	with new boolean[max + 1];
	\[2..length] = true;

	int n = ceil(sqrt(max));

	for (int i: 0..(n+1)) {
		if [i] : for (int j: (2 * i)..(max+1)) {
			[j] = false;
		}
	}

	int largest = max;
	while ![largest]: largest--;

	println %"The largest prime less than or equal to $max is $largest";
	
}
