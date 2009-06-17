include stdio;

class CandyGiver {

	static Int candyCounter = 0;
	static String[] candies = {"kinder", "lollypop", "chewing gum"};
	static Int total = 0;
	
	Int candyNumber;

	new() {

		candyNumber = candyCounter;
		candyCounter = candyCounter++ % 3;

	}

	func getCandy -> String {

		total++;
		printf("Hay, I'm the candy giver, I just a gave a %s, total is now %d\n", candies[candyNumber], total);
		return candies[candyNumber];

	}

}
