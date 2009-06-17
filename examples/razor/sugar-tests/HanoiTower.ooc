const int nDisks = 3;

func main {
	hanoiTower(nDisks, 'A', 'B', 'C');
}

func hanoiTower (int topN, char \(src, inter, dest)) {
	
		if (topN == 1) println %"Disk 1 from $src to $dest";
		else {
			// src to inter
			hanoiTower (topN - 1, src, dest, inter);
			// move bottom
			println %"Disk $topN from $src to $dest");
			//inter to dest
			hanoiTower (topN - 1, inter, src, dest);
		}
		
	}
}
