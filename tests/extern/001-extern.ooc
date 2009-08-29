include ./myheader

MyStruct: extern cover {
	Field : extern Int

	print: func {
		printf("Value = %d\n", Field)
	}
}

main: func {

	ms : MyStruct
	ms Field = 42
	ms print()

}
