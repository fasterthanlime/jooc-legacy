public class HanoiTower {
  static int nDisks = 3;

  public static void main(String[] args) {
    hanoiTower(nDisks, 'A', 'B', 'C');
  }

  public static void hanoiTower(int topN, char src, char inter, char dest) {
    if (topN == 1)
      System.out.println("Disk 1 from " + src + " to " + dest);
    else {
      // src to inter
      hanoiTower(topN - 1, src, dest, inter);
      // move bottom
      System.out.println("Disk " + topN + " from " + src + " to " + dest);
      //inter to dest
      hanoiTower(topN - 1, inter, src, dest);
    }
  }
}
