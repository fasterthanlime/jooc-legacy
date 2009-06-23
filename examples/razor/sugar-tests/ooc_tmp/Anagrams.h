#ifndef Anagrams_h
#define Anagrams_h


struct Anagrams;

#include <stdio.h>
#include "lang/String.h"
#include "OocLib.h"
Int main();


typedef struct Anagrams__class {
	String name;
	Void (*__doAnagram_Int)(struct Anagrams* , Int);
	Void (*__rotate_Int)(struct Anagrams* , Int);
	Void (*__display)(struct Anagrams* );
	
}* Anagrams__class;



typedef struct Anagrams {
	Anagrams__class class;
	Int size;
	Int newSize;
	String string;
	
}* Anagrams;


extern Anagrams__class Anagrams__classInstance;
extern Anagrams __Anagrams_new_String(String);
Void __Anagrams_doAnagram_Int(struct Anagrams* , Int);
Void __Anagrams_rotate_Int(struct Anagrams* , Int);
Void __Anagrams_display(struct Anagrams* );


#endif // Anagrams_h
