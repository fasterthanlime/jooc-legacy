extern func signal (Func)

func main {

	answer(func (Int) {
		printf("The answer is %d\n", 42)
	})

}

func answer(Func(Int) callback) {
	callback(42)
}
