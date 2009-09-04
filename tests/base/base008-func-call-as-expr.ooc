include time
TimeT: cover from time_t
srand: extern func (Int)
rand: extern func -> Int
time: extern func (TimeT) -> Int

main: func {

	srand(time(null))
	printf("The answer is %d\n", rand())

}
