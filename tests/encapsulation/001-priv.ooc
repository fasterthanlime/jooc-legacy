
Dog: class {

	name: private String
	age: Int

	setName: func (=name)
	getName: func -> String { name }

}

func main {

	dog := Dog new()
	dog setName("Dogbert")
	dog age = 18
	printf("I've a dog named %s and aged %d\n", dog getName(), dog age())

}
