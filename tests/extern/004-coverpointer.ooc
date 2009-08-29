include ./myheader

MyStruct: extern cover {
	field : extern(Field) Int

	print: func {
		printf("Struct value = %d\n", field)
	}
}

My: cover from MyStruct* {
	
	print: func {
		printf("Pointered value = %d\n", this@ field)
	}
}

main: func {

	ms : MyStruct
	ms field = 42
	ms print()
	
	my := ms& as My
	my print()

}
