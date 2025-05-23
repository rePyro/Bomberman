// package declarations and imports here;
public class Bomb extends Tile
{
  // variables
  private int fuse;
  
  public Bomb() {
    super("Bomb", true, true);
    // fuse = 5; // TODO: set fuse to appropiate number
  }

  // TODO: make Bomb functions with gameticks
  /*
  public boolean tryBreak(Tile[][] field, int row, int col) {
    try {
      if (field[row][col].getBreakable()) {
        field[row][col] = new Tile();
        return true;
      } else {
        return false;
      }
    }  
    catch (ArrayIndexOutOfBoundsException e) {
      return false;
    }
  }
  */
}
