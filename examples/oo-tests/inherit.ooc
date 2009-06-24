include stdio, stdlib;

abstract class Parent1 {

	Int value1;

	abstract func shout;

}

abstract class Parent2 from Parent1 {

	Int value2;

	implement shout {
		printf("Ayeoh, here Parent2.shout. Just doin' my job, mam..\n");
		printf("%d, %d, I'm a sportsman.\n", value1, value2);
	}

}

class Parent3 from Parent2 {

	Int value3;

	override shout {
		printf("%d, %d, %d, shouuuuuuuuuuuuuuuuuuuut!!!!\n", value1, value2, value3);
	}

}

func main {

	Parent3 p3 = new Parent3;
	p3.value1 = 1;
	p3.value2 = 2;
	p3.value3 = 3;
	printf("Finished initializing p3, casting...\n");
	
	Parent2 p2 = (Parent2) p3;
	printf("Casted!, now p2.shouting\n");
	p2.shout;
	printf("Doing things with ma ladybug friend.\n");
	p2.value2 = 69;
	printf("Shouted, now p3.shouting\n");
	p3.shout;
	
	Parent1 p1 = (Parent1) p3;
	p1.value1 = 42;
	p1.shout;
	printf("Shouted, now p3.shouting\n");
	p3.shout;

}
