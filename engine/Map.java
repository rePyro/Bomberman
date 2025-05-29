// package declarations and imports here;
import java.util.ArrayList;

public class Map 
{
  // variables
  private Tile[][] field;
  private int tileSize;
  private ArrayList<Bomb> bombList;
  private ArrayList<BombFireGroup> bombFireList;
  // constructor
  public Map() {
    field = new Tile[19][15]; // initialize empty field 9,15
    tileSize = 48;
    bombList = new ArrayList<Bomb>();
    bombFireList = new ArrayList<BombFireGroup>();

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
  //GAME TICK
  public void gameTick() {
    // check for explosions
    explodeCheck();
    // check for bomb fires
    fireTick();
  }

  // accessors
  public Tile[][] getField() {
    return field;
  }
  public Tile getTile(int row, int col) {
    return field[row][col];
  }
  public boolean isFree(int row, int col) {
    if (!withinR(row) || !withinC(col)) {
      return false; // out of bounds
    }
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
  public void fireTick() {
    for (int i = bombFireList.size() - 1; i >= 0; i--) {
        BombFireGroup group = bombFireList.get(i);
        group.tickFuse();
        if (group.getFuse() == 0) {
            // Remove only fires that are still present and belong to this group
            for (BombFire fire : new ArrayList<>(group.getFires())) {
                Tile tile = getTile(fire.getRow(), fire.getCol());
                if (tile instanceof BombFire && ((BombFire) tile).getGroup() == group) {
                    setTile(fire.getRow(), fire.getCol(), new Tile());
                }
            }
            bombFireList.remove(i);
        }
    }
}

public void addBombFireGroup(BombFireGroup group) {
    bombFireList.add(0, group);
  }

  //BombFire
  public void addBombFire(int row, int col) {
    Tile fire = new BombFire(row, col);
    setTile(row, col, fire);
  }
public void addBombFire(BombFireGroup group, int row, int col) {
    Tile oldTile = getTile(row, col);
    if (oldTile instanceof BombFire) {
        BombFire oldFire = (BombFire) oldTile;
        BombFireGroup oldGroup = oldFire.getGroup();
        if (oldGroup != null) {
            System.out.println("Removing old fire from group");
            oldGroup.getFires().remove(oldFire);
        }
    }
    BombFire fire = new BombFire(group, row, col); // create fire with group
    setTile(row, col, fire);                       // place it on the map
    group.getFires().add(fire);                    // add to group
}

  //EXPLOSIONS
  public void addBomb(int row, int col) {
    Bomb bomb = new Bomb(row, col);
    setTile(row, col, bomb);
    bombList.add(bomb);
}

  public void explodeCheck() {
    // 1. Tick all bombs ONCE
    for (int i = 0; i < bombList.size(); i++) {
        if (bombList.get(i).getFuse() > 0) {
            bombList.get(i).tickFuse();
        }
    }

    // 2. Now, repeatedly process all bombs with fuse == 0 (chain reactions)
    boolean exploded;
    do {
        exploded = false;
        for (int i = bombList.size() - 1; i >= 0; i--) {
            if (bombList.get(i).getFuse() == 0) {
                // Run Explosion Process
                BombFireGroup group = new BombFireGroup(5);
                addBombFireGroup(group);
                Bomb bomb = bombList.get(i);
                blowUp(bomb, 1, group);
                blowDown(bomb, 1, group);
                blowLeft(bomb, 1, group);
                blowRight(bomb, 1, group);
                addBombFire(group, bomb.getRow(), bomb.getCol());
                bombList.remove(i);
                exploded = true;
            }
        }
    } while (exploded);
}

  public void explode(Bomb bomb) {
    //Run Explosion Process
    BombFireGroup group = new BombFireGroup(5);
    addBombFireGroup(group);
    blowUp(bomb, 1, group);
    blowDown(bomb, 1, group);
    blowLeft(bomb, 1, group);
    blowRight(bomb, 1, group);
    addBombFire(group, bomb.getRow(), bomb.getCol());
    bombList.remove(bomb); // remove the bomb from the list after explosion
  }
  public void blowUp(Bomb bomb, int num, BombFireGroup group) {
    if (num <= bomb.getPower()) {
      int r = bomb.getRow(); int c = bomb.getCol();
      if (withinR(r-num) && withinC(c)) {
      if (field[r-num][c].getSolid() == false) {
        addBombFire(group, r-num, c);
        blowUp(bomb, num+1, group);
            }
            else if (field[r-num][c] instanceof Bomb) {
    // Chain reaction: set fuse to 0
    Bomb targetBomb = (Bomb)field[r-num][c];
    targetBomb.detonate();
} else if (field[r-num][c].getBreakable() == true) {
   setTile(r-num, c, new Tile());
}
          }
        }
      }
  
  public void blowDown(Bomb bomb, int num, BombFireGroup group) {
    if (num <= bomb.getPower()) {
      int r = bomb.getRow(); int c = bomb.getCol();
      if (withinR(r+num) && withinC(c)) {
      if (field[r+num][c].getSolid() == false) {
        addBombFire(group, r+num, c);
        blowDown(bomb, num+1, group);
            }
            else if (field[r+num][c] instanceof Bomb) {
    // Chain reaction: set fuse to 0
    Bomb targetBomb = (Bomb)field[r+num][c];
    targetBomb.detonate();
} else if (field[r+num][c].getBreakable() == true) {
    setTile(r+num, c, new Tile());
}
          }
        }
      }
  public void blowLeft(Bomb bomb, int num, BombFireGroup group) {
    if (num <= bomb.getPower()) {
      int r = bomb.getRow(); int c = bomb.getCol();
      if (withinR(r) && withinC(c-num)) {
      if (field[r][c-num].getSolid() == false) {
        addBombFire(group, r, c-num);
        blowLeft(bomb, num+1, group);
            }
            else if (field[r][c-num] instanceof Bomb) {
    // Chain reaction: set fuse to 0
    Bomb targetBomb = (Bomb)field[r][c-num];
    targetBomb.detonate();
} else if (field[r][c-num].getBreakable() == true) {
  setTile(r, c-num, new Tile());
}
          }
        }
      }

  public void blowRight(Bomb bomb, int num, BombFireGroup group) {
    if (num <= bomb.getPower()) {
      int r = bomb.getRow(); int c = bomb.getCol();
      if (withinR(r) && withinC(c+num)) {
      if (field[r][c+num].getSolid() == false) {
        addBombFire(group, r, c+num);
        blowRight(bomb, num+1, group);
            }
            else if (field[r][c+num] instanceof Bomb) {
    // Chain reaction: set fuse to 0
    Bomb targetBomb = (Bomb)field[r][c+num];
    targetBomb.detonate();
} else if (field[r][c+num].getBreakable() == true) {
    setTile(r, c+num, new Tile());
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
