// package declarations and imports here;
public class Map 
{
  // variables
  private Tile[][] field;

  // constructor
  public Map() {
    field = new Tile[15][9]; // initialize empty field

    // set Tiles for each position
    for (int row = 0; row < field.length; row++) {
      for (int col = 0; col < field[0].length; col++) {
        if (row == 0 || row == field.length - 1 || col == 0 || col == field[0].length - 1) {
          field[row][col] = new HardWall(); // HardWall the border (haha, trump)
        } else if (row % 2 == 0 && col % 2 == 0) {
          field[row][col] = new HardWall(); // HardWall the inside
        } else if 
                  ((row != 1 && col != 1) ||
                  (row != 1 && col != 2) ||
                  (row != 2 && col != 1) ||
                  (row != field.length - 3 && col != field.length - 2) ||
                  (row != field.length - 3 && col != field.length - 2) ||
                  (row != field.length - 3 && col != field.length - 2)) // skip spawn spaces
        {
          if ((int) (Math.random() * 10) < 8) { // 80% chance to...
          field[row][col] = new SoftWall(); // SoftWall the grid
          }
        }
      }
    }
  }

  // accessors
  public Tile[][] getField() {
    return field;
  }
  public Tile getTile(int row, int col) {
    return field[row][col];
  }
  public void printMap() {
    for (int row = 0; row < field.length; row++) {
      for (int col = 0; col < field[0].length; col++) {
        if (field[row][col] instanceof HardWall) {
          System.out.print("H");
        } else if (field[row][col] instanceof SoftWall) {
          System.out.print("S");
        } else {
          System.out.print(" ");
        }
        System.out.print(" ");
      }
      System.out.println();
    }
  }
}
