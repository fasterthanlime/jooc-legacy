BlahStruct: cover from struct blah {
  i: extern Int
}

Blah: cover from BlahStruct*

b : Blah = getBlah()
b@ i = 42 // no such field 'i' in Blah (it should look into BlahStruct)
