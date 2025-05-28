public class BombFire extends Tile {
    // package declarations and imports here;
public class Bomb extends Tile
{
  // variables
  private int fuse;
  
  public Bomb(int row, int col) {
    super("Bomb", false, false);
    fuse = 5; // TODO: set fuse to appropiate number
  }
  public void tickFuse() {
    fuse--;
  }

  public int getFuse() {
    return fuse;
  }
}
}
