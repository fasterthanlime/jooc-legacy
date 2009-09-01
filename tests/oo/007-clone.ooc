MyClass: class {
    a, b: Int

    init: func (=a, =b) {
        printf("Called MyClass.init(%d, %d)\n", a, b)
    }

    clone: func -> This {
		printf("Cloning a %s, (function defined in %s)\n", this class name, This name)
        obj := this class alloc() as This
        obj init(1, 2)
        obj
    }
	
	print: func {
		printf("Hi, I'm a MyClass with values (%d, %d)\n", a, b)
	}
}

MySubClass: class extends MyClass {
	c := 42
	
	init: func ~three (.a, .b, =c) {
		super(a, b)
		c = 3
	}
	
	print: func {
		printf("Hi, I'm a MySubClass with values (%d, %d, %d)\n", a, b, c)
	}
}

main: func {

	mc := MyClass new(42, 39)
	mc print()
	
	mc2 := mc clone()
	mc2 print()
	
	ms := MySubClass new(31, 26, 90)
	ms print()
	
	ms2 := ms clone()
	ms2 print()

}
