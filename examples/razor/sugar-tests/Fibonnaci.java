/**
 * This program prints out the first 20 numbers in the Fibonacci sequence. Each
 * term is formed by adding together the previous two terms in the sequence,
 * starting with the terms 1 and 1.
 */
public class Fibonacci {
  public static void main(String[] args) {
    int n0 = 1, n1 = 1, n2; // Initialize variables
    System.out.print(n0 + " " + // Print first and second terms
        n1 + " "); // of the series

    for (int i = 0; i < 18; i++) { // Loop for the next 18 terms
      n2 = n1 + n0; // Next term is sum of previous two
      System.out.print(n2 + " "); // Print it out
      n0 = n1; // First previous becomes 2nd previous
      n1 = n2; // And current number becomes previous
    }
    System.out.println(); // Terminate the line
  }
}
