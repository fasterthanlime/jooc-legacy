#ifndef math_Vector2f_h
#define math_Vector2f_h


struct math_Vector2f;

#include <math.h>
#include <stdio.h>
#include "../OocLib.h"


typedef struct math_Vector2f__class {
	String name;
	Float (*__squaredLength)(struct math_Vector2f* );
	Float (*__length)(struct math_Vector2f* );
	Void (*__reverse)(struct math_Vector2f* );
	Void (*__normalize)(struct math_Vector2f* );
	Void (*__scale_Float)(struct math_Vector2f* , Float);
	Void (*__set_Float_Float)(struct math_Vector2f* , Float, Float);
	Void (*__set_math_Vector2f)(struct math_Vector2f* , struct math_Vector2f* );
	Void (*__add_math_Vector2f)(struct math_Vector2f* , struct math_Vector2f* );
	Void (*__sub_math_Vector2f)(struct math_Vector2f* , struct math_Vector2f* );
	Void (*__mul_Float)(struct math_Vector2f* , Float);
	Void (*__div_Float)(struct math_Vector2f* , Float);
	Void (*__add_Float_Float)(struct math_Vector2f* , Float, Float);
	Void (*__add_Int_Int)(struct math_Vector2f* , Int, Int);
	Void (*__sub_Float_Float)(struct math_Vector2f* , Float, Float);
	Void (*__sub_Int_Int)(struct math_Vector2f* , Int, Int);
	String (*__repr)(struct math_Vector2f* );
	
}* math_Vector2f__class;



typedef struct math_Vector2f {
	math_Vector2f__class class;
	Float x;
	Float y;
	
}* math_Vector2f;


extern Float math_Vector2f_EPSILON;
extern math_Vector2f__class math_Vector2f__classInstance;
extern math_Vector2f __math_Vector2f_new();
extern math_Vector2f __math_Vector2f_new_Float_Float(Float, Float);
extern math_Vector2f __math_Vector2f_new_math_Vector2f(struct math_Vector2f* );
Float __math_Vector2f_squaredLength(struct math_Vector2f* );
Float __math_Vector2f_length(struct math_Vector2f* );
Void __math_Vector2f_reverse(struct math_Vector2f* );
Void __math_Vector2f_normalize(struct math_Vector2f* );
Void __math_Vector2f_scale_Float(struct math_Vector2f* , Float);
Void __math_Vector2f_set_Float_Float(struct math_Vector2f* , Float, Float);
Void __math_Vector2f_set_math_Vector2f(struct math_Vector2f* , struct math_Vector2f* );
Void __math_Vector2f_add_math_Vector2f(struct math_Vector2f* , struct math_Vector2f* );
Void __math_Vector2f_sub_math_Vector2f(struct math_Vector2f* , struct math_Vector2f* );
Void __math_Vector2f_mul_Float(struct math_Vector2f* , Float);
Void __math_Vector2f_div_Float(struct math_Vector2f* , Float);
Void __math_Vector2f_add_Float_Float(struct math_Vector2f* , Float, Float);
Void __math_Vector2f_add_Int_Int(struct math_Vector2f* , Int, Int);
Void __math_Vector2f_sub_Float_Float(struct math_Vector2f* , Float, Float);
Void __math_Vector2f_sub_Int_Int(struct math_Vector2f* , Int, Int);
String __math_Vector2f_repr(struct math_Vector2f* );


#endif // math_Vector2f_h
