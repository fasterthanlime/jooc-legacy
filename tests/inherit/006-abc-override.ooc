A: class {
    foo: func {}
}

B: class extends A {
    foo: func { "yay" println() }
}

C: class extends B {}

main: func {
	objs := [A new as Func, B new, C new]
	for (i in 0..3) {
		f := objs[i]
		f() as A foo()
	}
}

Func: extern cover
