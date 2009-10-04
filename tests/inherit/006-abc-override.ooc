A: class {
    foo: func { "yohow" println() }
}

B: class extends A {
    foo: func { "yay" println() }
}

C: class extends B {}

main: func {
	objs : Pointer[] = [A_new, B_new, C_new]
	for (i in 0..3) {
		f := objs[i] as Func -> A
		f() as A foo()
	}
}

A_new: extern func -> A
B_new: extern func -> A
C_new: extern func -> A
