// package declarations and imports here;
public class HardWall extends Tile
{
  public HardWall() {
    super("HardWall", true, false);
  }
  public HardWall(int row, int col) {
    super("HardWall", true, false);
    setRowIndex(row); setColIndex(col);
  }
}
