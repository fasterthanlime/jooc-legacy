#ifndef lang_String_h
#define lang_String_h


#include <stdio.h>
#include <math.h>
#include "Char.h"
#include "../OocLib.h"

// Cover definition of 'lang.String'
Int __lang_String_length(String);
Bool __lang_String_equals_String(String, String);
Int __lang_String_toInt(String);
Long __lang_String_toLong(String);
LLong __lang_String_toLLong(String);
Double __lang_String_toDouble(String);
Bool __lang_String_isEmpty(String);
Bool __lang_String_startsWith_String(String, String);
Bool __lang_String_endsWith_String(String, String);
Int __lang_String_indexOf_Char(String, Char);
Int __lang_String_lastIndexOf_Char(String, Char);
String __lang_String_subrange_Int_Int(String, Int, Int);
String __lang_String_reverse(String);


#endif // lang_String_h
