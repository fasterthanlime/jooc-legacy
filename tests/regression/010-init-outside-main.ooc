Vector3f: class {
  x, y, z : Float
  
  init: func(=x, =y, =z) {}
  println: func { printf("(%.2f, %.2f, %.2f)\n", x, y, z) }
}
 
Vector3f new(3.14, 6.18, 42.0) println()
