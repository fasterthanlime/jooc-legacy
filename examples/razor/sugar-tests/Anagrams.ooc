//with io.stdout.\(print, println);
include stdio;

Int size;
String string;

func main {
	
	string = "Java Source and Support";
	doAnagram(string.length);
	
}

func doAnagram (Int newSize) {
	
	if (newSize == 1) return; // if too small, return
		
	// for each position,
	for (Int i: 0..newSize) {
		
		doAnagram (newSize - 1); // anagram remaining
		if (newSize == 2) { display }
		rotate(newSize); // rotate word
		
	}
	
}

// rotate left all chars from position to end
func rotate (Int newSize) {

	Int position = size - newSize;

	// save first letter
	Char temp = string[position];
	
	//shift others left
	for (Int i: (position + 1)..size) {
		string[i - 1] = string[i];
	}
		
	//put first on right
	string[i - 1] = temp;
	
}

func display {
	
	static Int count = 0;
	printf(%"${++count} $string\n");
	
}
