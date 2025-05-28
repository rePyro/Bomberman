// package declarations and imports here;
public class Bomb extends Tile
{
  // variables
  private int fuse;
  private int row;
  private int col;
  private int power;
  
  public Bomb(int row, int col) {
    super("Bomb", true, true);
    this.row = row;
    this.col = col;
    fuse = 5; // TODO: set fuse to appropiate number
    power = 2;
  }
  public void tickFuse() {
    fuse--;
  }

  public int getFuse() {
    return fuse;
  }
  public int getPower() {
    return power;
  }
  public int getRow() {
    return row;
  }
  public int getCol() {
    return col;
  }


  // TODO: make Bomb functions with gameticks
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

  public void explode(Tile[][] field) {
    for (int r = 1; r <= power; r++)
    {if (tryBreak(field, row +r, col)) {
      

    }}

  }

}
