// package declarations and imports here;
public class SoftWall extends Tile
{private boolean exploded = false;
  public SoftWall() {
    super("SoftWall", true, true);
  }
  public void explodeTile() {
    exploded = true;
  }
}
