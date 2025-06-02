// package declarations and imports here;
public class Bomb extends Tile {
  // variables
  private int fuse;
  private int row;
  private int col;
  private int power;
  private int tileSize = 48; // Assuming tileSize is 32 pixels, adjust as needed
  
  public Bomb(int row, int col) {
    super("Bomb", true, true);
    this.row = row;
    this.col = col;
    fuse = 180; 
    power = 1;
  }
  //clone
  @Override
public Bomb clone() {
    Bomb cloned = (Bomb) super.clone();
    // Copy primitive fields (already done by super.clone()), but do it explicitly if needed:
    cloned.fuse = this.fuse;
    cloned.row = this.row;
    cloned.col = this.col;
    cloned.power = this.power;
    // If Bomb ever contains mutable objects, clone them here as well.
    return cloned;
}
//updates
  public void tickFuse() {
    fuse--;
  }
  public void detonate() {
    this.fuse = 0;
}
// accessors
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
  public int getY() {
    return row * tileSize;
  }
  public int getX() {
    return col * tileSize;
  }
  //Lazy
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
  public void setFuse(int fuse) {
    this.fuse = fuse;
  }
}
