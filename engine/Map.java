// package declarations and imports here;
import java.util.ArrayList;

public class Map 
{
  // variables
  private Tile[][] field;
  private int tileSize;
  private ArrayList<Bomb> bombList;
  private ArrayList<BombFire> bombFireList;
  // constructor
  public Map() {
    field = new Tile[9][15]; // initialize empty field
    tileSize = 48;
    bombList = new ArrayList<Bomb>();
    bombFireList = new ArrayList<BombFire>();

    // set Tiles for each position
    for (int row = 0; row < field.length; row++) {
      for (int col = 0; col < field[0].length; col++) {
        if (row == 0 || row == field.length - 1 || col == 0 || col == field[0].length - 1) {
          field[row][col] = new HardWall(); // HardWall the border (haha, trump)
        } else if (row % 2 == 0 && col % 2 == 0) {
          field[row][col] = new HardWall(); // HardWall the inside
        } else if 
                  ((row != 1 || col != 1) &&
                  (row != 1 || col != 2) &&
                  (row != 2 || col != 1) &&
                  (row != field.length - 3 || col != field[0].length - 2) &&
                  (row != field.length - 2 || col != field[0].length - 2) &&
                  (row != field.length - 2 || col != field[0].length - 3)) // skip spawn spaces
        {
          if ((int) (Math.random() * 10) < 7) { // 70% chance to...
            field[row][col] = new SoftWall(); // SoftWall the grid
          } else {
            field[row][col] = new Tile(); // Tile the empty spaces
          }
        } else if ((row == 1 && col == 1) || (row == field.length - 2 && col == field[0].length - 2)) {
          field[row][col] = new SpawnTile(); // Tile the spawns
        } else {
          field[row][col] = new Tile(); // Tile the guaranteed empty spaces arond spawn
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
  public boolean isFree(int row, int col) {
    return !field[row][col].getSolid();
  }
  // mutators
  public void setTile(int row, int col, Tile tile) {
    field[row][col] = tile;
  }
  //Converter
  public int rowToY( int row) {
    return (int)((row+0.5)*tileSize);
  }
  public int colToX(int col) {
    return (int)((col+0.5)*tileSize);
  }
  //Lazy
  public boolean withinR(int r) {
    if (0 <= r && r < field.length) {
      return true;
    }
    else return false;
  }
  public boolean withinC(int c) {
    if (0 <= c && c < field[0].length) {
      return true;
    }
    else return false;
  }



  //Fire
  //public void fireTick() {
    //for (int i = 0; i < fireBombList.size())
 // }



  //EXPLOSIONS
  public void addBomb(int row, int col) {
    Tile bomb = new Bomb(row, col);
    setTile (row, col, bomb);
    Bomb bomb2 = new Bomb(row, col);
    bombList.add(bomb2);
  }

  public void explodeCheck() {
    for (int i = bombList.size()-1; i >= 0; i--) {
      System.out.print(bombList.get(i).getFuse());
      if (bombList.get(i).getFuse() == 0) {
        //Run Explosion Process
        Bomb bomb = bombList.get(i);
        blowUp(bomb, 1);
        blowDown(bomb, 1);
        blowLeft(bomb, 1);
        blowRight(bomb, 1);
        Tile explodedBomb = new BombFire();
        setTile(bomb.getRow(), bomb.getCol(), explodedBomb);
        bombList.remove(i);
        }
        else bombList.get(i).tickFuse();
      }
      System.out.println();
    }
  public void blowUp(Bomb bomb, int num) {
    System.out.println("Running BlowUp()");
    if (num <= bomb.getPower()) {
      int r = bomb.getRow(); int c = bomb.getCol();
      if (withinR(r-num) && withinC(c)) {
      if (field[r-num][c].getSolid() == false) {
        Tile fire = new BombFire();
              setTile(r-num, c, fire);
        System.out.println("Free Space Above");
        blowUp(bomb, num+1);
            }
            else if (field[r-num][c].getBreakable() == true) {
              Tile exploded = new Tile();
              setTile(r-num, c, exploded);
              System.out.println("blew up a tile: "+num);
            }
          }
        }
      }
  public void blowDown(Bomb bomb, int num) {
    if (num <= bomb.getPower()) {
      int r = bomb.getRow(); int c = bomb.getCol();
      if (withinR(r+num) && withinC(c)) {
      if (field[r+num][c].getSolid() == false) {
        Tile fire = new BombFire();
              setTile(r+num, c, fire);
        blowDown(bomb, num+1);
            }
            else if (field[r+num][c].getBreakable() == true) {
              Tile exploded = new Tile();
              setTile(r+num, c, exploded);
            }
          }
        }
      }
  public void blowLeft(Bomb bomb, int num) {
    if (num <= bomb.getPower()) {
      int r = bomb.getRow(); int c = bomb.getCol();
      if (withinR(r) && withinC(c-num)) {
      if (field[r][c-num].getSolid() == false) {
        Tile fire = new BombFire();
              setTile(r, c-num, fire);
        blowLeft(bomb, num+1);
            }
            else if (field[r][c-num].getBreakable() == true) {
              Tile exploded = new Tile();
              setTile(r, c-num, exploded);
            }
          }
        }
      }

  public void blowRight(Bomb bomb, int num) {
    if (num <= bomb.getPower()) {
      int r = bomb.getRow(); int c = bomb.getCol();
      if (withinR(r) && withinC(c+num)) {
      if (field[r][c+num].getSolid() == false) {
        Tile fire = new BombFire();
              setTile(r, c+num, fire);
        blowRight(bomb, num+1);
            }
            else if (field[r][c+num].getBreakable() == true) {
              Tile exploded = new Tile();
              setTile(r, c+num, exploded);
              System.out.println("blew up a tile: "+num);
            }
          }
        }
      }


  // visual aid
  public void printMap() {
    for (int row = 0; row < field.length; row++) {
      for (int col = 0; col < field[0].length; col++) {
        if (field[row][col] instanceof HardWall) {
          System.out.print("H");
        } else if (field[row][col] instanceof SoftWall) {
          System.out.print("S");
        } else if (field[row][col] instanceof SpawnTile) {
          System.out.print("A");
        } else if (field[row][col] instanceof Tile) {
          System.out.print("T");
        }
        System.out.print(" ");
      }
      System.out.println();
    }
  }
}
