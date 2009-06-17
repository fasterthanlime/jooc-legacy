include stdio;

class LikeWise {

	func dump {
		printf("Dump dumpadaddy dump.\n");
	}

	func swoosh {
		dump; // *should# call the function now!
	}

}

func main {

	new LikeWise.swoosh;

}
