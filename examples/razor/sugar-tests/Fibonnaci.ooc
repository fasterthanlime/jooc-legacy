/**
 * This program prints out the first 20 numbers in the Fibonacci sequence. Each
 * term is formed by adding together the previous two terms in the sequence,
 * starting with the terms 1 and 1.
 */
func main {
	
		int n0 = 1, n1 = 1, n2;
		print %"$n0 $n1 ";

		for (int i: 0..18) {
			n2 = n1 + n0;
			print %"$n2 ";
			\(n0, n1) = \(n1, n2);
		}
		
		println;

}
