signal: extern func (Func)

main: func {

	answer(func (arg: Int) {
		printf("The answer is %d\n", arg)
	})

}

answer: func (callback: Func (Int)) {
	callback(42)
}
