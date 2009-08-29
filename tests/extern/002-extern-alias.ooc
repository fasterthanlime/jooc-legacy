include ./myheader

MyStruct: extern cover {
	field : extern(Field) Int

	print: func {
		printf("Value = %d\n", field)
	}
}

main: func {

	ms : MyStruct
	ms field = 42
	ms print()

}
