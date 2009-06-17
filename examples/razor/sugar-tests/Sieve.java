/**
 * This program computes prime numbers using the Sieve of Eratosthenes
 * algorithm: rule out multiples of all lower prime numbers, and anything
 * remaining is a prime. It prints out the largest prime number less than or
 * equal to the supplied command-line argument.
 */
public class Sieve {
	
  public static void main(String[] args) {
    // We will compute all primes less than the value specified on the
    // command line, or, if no argument, all primes less than 100.
    int max = 100; // Assign a default value
    try {
      max = Integer.parseInt(args[0]);
    } // Parse user-supplied arg
    catch (Exception e) {
    } // Silently ignore exceptions.

    // Create an array that specifies whether each number is prime or not.
    boolean[] isprime = new boolean[max + 1];

    // Assume that all numbers are primes, until proven otherwise.
    for (int i = 0; i <= max; i++)
      isprime[i] = true;

    // However, we know that 0 and 1 are not primes. Make a note of it.
    isprime[0] = isprime[1] = false;

    // To compute all primes less than max, we need to rule out
    // multiples of all integers less than the square root of max.
    int n = (int) Math.ceil(Math.sqrt(max)); // See java.lang.Math class

    // Now, for each integer i from 0 to n:
    //   If i is a prime, then none of its multiples are primes,
    //   so indicate this in the array. If i is not a prime, then
    //   its multiples have already been ruled out by one of the
    //   prime factors of i, so we can skip this case.
    for (int i = 0; i <= n; i++) {
      if (isprime[i]) // If i is a prime,
        for (int j = 2 * i; j <= max; j = j + i)
          // loop through multiples
          isprime[j] = false; // they are not prime.
    }

    // Now go look for the largest prime:
    int largest;
    for (largest = max; !isprime[largest]; largest--)
      ; // empty loop body

    // Output the result
    System.out.println("The largest prime less than or equal to " + max
        + " is " + largest);
  }
  
}
