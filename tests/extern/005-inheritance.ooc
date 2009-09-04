include ./animals

Animal: extern cover {
	name: extern String
}

Dog: extern cover extends Animal {
	species: extern String
}

main: func {
	d : Dog
	d name = "Lola"
	d species = "Caniche"
	printf("Dog name = %s, species = %s\n", d name, d species)
	print(d as Animal)
}

print: func ~animal (a : Animal) {
	printf("Animal name = %s\n", a name)
}
