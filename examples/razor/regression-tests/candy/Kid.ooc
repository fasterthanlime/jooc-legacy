include stdlib, stdio, time;
import CandyGiver;

class Kid {

	new() {

		printf("Hayo, I'm a kid and I'm asking for candyyyy!\n");
		srand(time(null));
		CandyGiver giver = new CandyGiver;
		for(Int i: 0..(rand() % 15)) {
			giver.getCandy;
		}

	}

}
