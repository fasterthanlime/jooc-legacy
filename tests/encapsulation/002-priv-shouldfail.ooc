
Dog: class {

	name: String | private
	age: Int

	setName: func (=name)
	getName: func -> String { name }

}

func main {

	dog := Dog new()
	dog name = "Dogbert" // ERROR
	dog age = 18
	printf("I've a dog named %s and aged %d\n", dog getName(), dog age())

}
