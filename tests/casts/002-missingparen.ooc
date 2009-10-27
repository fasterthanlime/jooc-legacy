use math

// vector.ooc:
Vector3f: cover {
  x, y, z : Float
 
  length: func -> Float { sqrt(x * x + y * y + z * z) }
}
 
main: func {
 diagonal : Vector3f
 diagonal x = 1.0
 diagonal y = 1.0
 diagonal z = 1.0
 printf("The length of a cube's long diagonal is: %.2f\n", diagonal length())
}
