Int: cover from int {

	max: func (other: Int) -> Int {
		if(this > other) return this;
		return other;
	}

}

main: func {

	value := 24
	printf("The greatest of 24 and 42 is %d\n", value max(42));

}
