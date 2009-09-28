

Chien: class {

	nom: String
	
	init: func {
		
		nom = "Fido"
		
	}
	
	setNom: func (nom: String) {
		
		printf("this nom == %s, nom == %s\n", this nom, nom)
		this nom = nom
		
	}

}


main: func {
	
	chien := Chien new()
	
	printf("Nom du chien = %s\n", chien nom)
	
	chien setNom("Dogbert")
	
	printf("Maintenant, nom du chien = %s\n", chien nom)
	
}
