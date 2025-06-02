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
    fuse = 60; 
    this.row = row;
    this.col = col;
  }
  public BombFire(BombFireGroup group, int row, int col) {
    super("BombFire", false, false);
    fuse = 60; 
    this.row = row;
    this.col = col;
    this.group = group;
    group.addFire(this);
  }
  // clone
  @Override
public BombFire clone() {
    BombFire cloned = (BombFire) super.clone();
    // Do not clone the group reference here; it will be set by BombFireGroup.clone()
    cloned.fuse = this.fuse;
    cloned.row = this.row;
    cloned.col = this.col;
    cloned.group = null; // Will be set by BombFireGroup.clone()
    return cloned;
}
  public int getRow() {
    return row;
  }
  public int getCol() {
    return col;
  }
  public BombFireGroup getGroup() {
    return group;
  }

}
