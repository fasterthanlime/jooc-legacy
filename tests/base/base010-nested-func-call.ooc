include time
TimeT: cover from time_t
srand: extern func (Int)
rand: extern func -> Int
time: extern func (TimeT) -> Int

number: func (max : Int) -> Int {
	
	srand(time(null))
	return (rand() + 1) % max
	
}

main: func {

	printf("The answer is %d\n", number(number(number(number(number((2500)))))))

}
