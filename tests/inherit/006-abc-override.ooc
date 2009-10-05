A: class {
    foo: func { "yohow" println() }
}

B: class extends A {
    foo: func { "yay" println() }
}

C: class extends B {}

main: func {
	objs : Pointer[] = [A new, B new, C new]
	for (i in 0..3) {
		f := objs[i] as Func -> A
		f() as A foo()
	}
}
