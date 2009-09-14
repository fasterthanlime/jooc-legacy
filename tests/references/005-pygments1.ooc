main: func {
  ptr := gc_malloc(Int size) as Int*
  ptr@ = 42
  printf("ptr's value is %d\n", ptr@)
  add(ptr, 3)
  printf("ptr's value is now %d\n", ptr@)
}
 
add: func(ptr: Int*, value: Int) {
  ptr@ += value
}
