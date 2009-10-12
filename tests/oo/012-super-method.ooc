
Animal: class {
	
	poke: func {
		"Animal pookadibooh!" println()
	}
	
}


Dog: class extends Animal {
	
	poke: func {
		super poke()
		"Dog pokidi-poke" println()
	}
	
}


Dog new() poke()
