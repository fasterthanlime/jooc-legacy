printType: func <T> (param: T) {
  printf("Got param of type %s and size %d\n", T name, T size)
  if(T == Int) printf("It's an Int! and its value is %d\n", param as Int)
  else if(T == Char) printf("It's a char! and its value is '%c'\n", param as Char)
}

printType('c')
printType(42)
