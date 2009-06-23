//with io.stdout.\(print, println);
include stdio;
import lang.String;

func main {
	
	new Anagrams("Java Source and Support");
	
}

class Anagrams {

	Int size;
	Int newSize;
	String string;	

	new(=string) {
		
		size = this.string.length;
		doAnagram(size);
		
	}
	
	func doAnagram(Int newSize) {
		
		if (newSize == 1) return; // if too small, return
			
		// for each position,
		for (Int i: 0..newSize) {
			
			doAnagram (newSize - 1); // anagram remaining
			if (newSize == 2) { display; }
			rotate(newSize); // rotate word
			
		}
		
	}

	// rotate left all chars from position to end
	func rotate(Int newSize) {

		Int i;
		Int position = size - newSize;

		// save first letter
		Char temp = string[position];
		
		//shift others left
		for(i = position + 1; i < size; i++) {
			string[i - 1] = string[i];
		}
			
		//put first on right
		string[i - 1] = temp;
		
	}

	func display {
		
		static Int count = 0;
		//printf(%"${++count} $string\n");
		count++;
		printf("%d %s\n", count, string);
		
	}

}
