import structs/HashMap

Container: class {

  getValue: func <T> (T: Class) -> T {
    printf("size = %zd, instanceSize = %zd\n", T size, T instanceSize)
	
	// FIXME the 'ret' temp variable shouldn't be needed. But it is.
	ret := HashMap<Value> new()
	return ret
  }

}

Value: class {}

main: func {

	cont := Container new() getValue(HashMap<Value>)

}
