public class BombFire extends Tile {
    // package declarations and imports here;

  // variables
  private int fuse;
  int row;
  int col;
  private BombFireGroup group;

public void setGroup(BombFireGroup group) {
    this.group = group;
}

public int getFuse() {
    return group != null ? group.getFuse() : 0;
}
  
  public BombFire(int row, int col) {
    super("BombFire", false, false);
    fuse = 3; // TODO: set fuse to appropiate number
    this.row = row;
    this.col = col;
  }
  public BombFire(BombFireGroup group, int row, int col) {
    super("BombFire", false, false);
    fuse = 3; // TODO: set fuse to appropiate number
    this.row = row;
    this.col = col;
    this.group = group;
    group.addFire(this);
  }
  public int getRow() {
    return row;
  }
  public int getCol() {
    return col;
  }

}
