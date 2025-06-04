// package declarations and imports here;
import java.util.ArrayList;

public class Map 
{
  // variables
  private Tile[][] field;
  private int tileSize;
  private ArrayList<Bomb> bombList;
  private ArrayList<BombFireGroup> bombFireList;
  private ArrayList<SoftWall> softWallUpdates;
  private int fireFuse = 60; // default fuse for fire, can be changed later
  private int bombFuse = 180; // default fuse for bomb, can be changed later // default power for bomb, can be changed later
  private int currentTick = 0; // tick counter for game ticks
  private int ticksPerSecond = 60; // number of ticks per second, can be changed later
  // constructor
  public Map() {
    field = new Tile[9][15]; // initialize empty field
    tileSize = 48;
    bombList = new ArrayList<Bomb>();
    bombFireList = new ArrayList<BombFireGroup>();
    softWallUpdates = new ArrayList<SoftWall>();

    // set Tiles for each position
    for (int row = 0; row < field.length; row++) {
      for (int col = 0; col < field[0].length; col++) {
        if (row == 0 || row == field.length - 1 || col == 0 || col == field[0].length - 1) {
          field[row][col] = new HardWall(row, col); // HardWall the border (haha, trump)
        } else if (row % 2 == 0 && col % 2 == 0) {
          field[row][col] = new HardWall(row, col); // HardWall the inside
        } else if 
                  ((row != 1 || col != 1) &&
                  (row != 1 || col != 2) &&
                  (row != 2 || col != 1) &&
                  (row != field.length - 3 || col != field[0].length - 2) &&
                  (row != field.length - 2 || col != field[0].length - 2) &&
                  (row != field.length - 2 || col != field[0].length - 3)) // skip spawn spaces
        {
          if ((int) (Math.random() * 10) < 7) { // 70% chance to...
            field[row][col] = new SoftWall(row, col); // SoftWall the grid
          } else {
            field[row][col] = new Tile(row, col); // Tile the empty spaces
          }
        } else if ((row == 1 && col == 1) || (row == field.length - 2 && col == field[0].length - 2)) {
          field[row][col] = new Tile(); // Tile the spawns (removed for now)
        } else {
          field[row][col] = new Tile(); // Tile the guaranteed empty spaces arond spawn
        }
      }
    }
  }
  //Clone/DeepCopy
  public Map(Map other) {
    // Deep copy the field (initially with shallow clones)
    this.field = new Tile[other.field.length][other.field[0].length];
    for (int r = 0; r < field.length; r++) {
        for (int c = 0; c < field[0].length; c++) {
            this.field[r][c] = other.field[r][c].clone();
        }
    }
    this.tileSize = other.tileSize;

    // Clone bombs
    this.bombList = new ArrayList<>();
    for (Bomb b : other.bombList) {
        this.bombList.add(b.clone());
    }

    // Clone BombFireGroups and BombFires (order matters)
    this.bombFireList = new ArrayList<>();
    for (int g = 0; g < other.bombFireList.size(); g++) {
        BombFireGroup origGroup = other.bombFireList.get(g);
        BombFireGroup clonedGroup = origGroup.clone();
        this.bombFireList.add(clonedGroup);

        // For each fire in the group, update the field reference if it matches the original
        for (int f = 0; f < origGroup.getFires().size(); f++) {
            BombFire origFire = origGroup.getFires().get(f);
            BombFire clonedFire = clonedGroup.getFires().get(f);
            int row = origFire.getRow();
            int col = origFire.getCol();
            // Only replace if the original fire is at this location
            if (other.field[row][col] == origFire) {
                this.field[row][col] = clonedFire;
            }
        }
    }
    // Clone soft wall updates
    this.softWallUpdates = new ArrayList<>();
    for (SoftWall tile : other.softWallUpdates) {
        this.softWallUpdates.add(tile.clone());
    }
    this.fireFuse = other.fireFuse;
    this.bombFuse = other.bombFuse;
    this.currentTick = other.currentTick;
    this.ticksPerSecond = other.ticksPerSecond;
}
  //GAME TICK
  public void gameTick() {
      mapUpdate(); // update the map
    }
  public void mapUpdate() {
    // 1. Check for explosions
    explodeCheck();
    // 2. Tick all fires
    fireTick();
    // 3. Update soft walls
    tileUpdate();
  }
  public void tileUpdate() {
    // Update all tiles in the field
    if (softWallUpdates == null) {
      return; // No soft walls to update
    }
    for (int i = softWallUpdates.size() - 1; i >= 0; i--) {
      SoftWall tile = softWallUpdates.get(i);
      if (tile.getCurrentFrame() == 0) {
        int pCount = 25; int pSpeed = 50; int pPower = 75;
        int choice = (int)(100*Math.random())+1;
        Tile tileReplace = new Tile();
        if (choice <= pCount) {tileReplace = new CountUpgrade(); }
        else if (choice <= pSpeed) {tileReplace = new SpeedUpgrade(); }
        else if (choice <= pPower) {tileReplace = new PowerUpgrade(); }
        tileReplace.setRowIndex(tile.getRowIndex());tileReplace.setColIndex(tile.getColIndex());
      setTile(tile.getRowIndex(), tile.getColIndex(), tileReplace);
      softWallUpdates.remove(i);
      }
      else tile.update();
      }
    }
  public void mapUpdate(int ticks) {
    int i = 0;
    while (i < ticks) {
      mapUpdate();
      i++;
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
    if (!withinR(row) || !withinC(col)) {
      return false; // out of bounds
    }
    return !field[row][col].getSolid();
  }
  public int getTileSize() {
    return tileSize;
  }
  public ArrayList<Bomb> getBombList() {
    return bombList;
  }
  public ArrayList<BombFireGroup> getBombFireList() {
    return bombFireList;
  }
  public int getFireFuse() {
    return fireFuse;
  }
  public int getBombFuse() {
    return bombFuse;
  }
  public int getHeight() {
    return field.length * tileSize;
  }
  public int getWidth() {
    return field[0].length * tileSize;
  }
  // mutators
  public void setTile(int row, int col, Tile tile) {
    field[row][col] = tile;
  }
  //Converter
  public int rowToY( int row) {
    return row * tileSize;
  }
  public int colToX(int col) {
    return col * tileSize;
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
            oldGroup.getFires().remove(oldFire);
        }
    }
    BombFire fire = new BombFire(group, row, col); // create fire with group
    setTile(row, col, fire);                       // place it on the map
    group.getFires().add(fire);                    // add to group
}
  //EXPLOSIONS
  public void addBomb(int row, int col) {
    if (!field[row][col].getTileType().equals("Bomb")) { // check if the tile is not already a bomb
      Bomb bomb = new Bomb(row, col);
      setTile(row, col, bomb);
      bombList.add(bomb);
    }
  }
  public void addBomb(Player player) {
    if (player.canAddBomb())
    {if (!field[player.getRow()][player.getCol()].getTileType().equals("Bomb")) { // check if the tile is not already a bomb
      System.out.println("Spawning a bomb");
      Bomb bomb = new Bomb(player);
      setTile(player.getRow(), player.getCol(), bomb);
      bombList.add(bomb);
      System.out.println("Bomb added to player: " + player.getPlayerNumber());
    }
  }else {System.out.println("Can't add a bomb lol goober"+player.getBombsPlaced());
  }
}
  public void addBomb(Player player, boolean real) {
    if (player.canAddBomb())
    {if (!field[player.getRow()][player.getCol()].getTileType().equals("Bomb")) { // check if the tile is not already a bomb
      if (real) {System.out.println("Spawning a bomb");}
      Bomb bomb = new Bomb(player, real);
      setTile(player.getRow(), player.getCol(), bomb);
      bombList.add(bomb);
    }
  }else {System.out.println("Can't add a bomb lol goober"+player.getBombsPlaced());
  }
}
  


private static final int[][] DIRECTIONS = {
    {-1, 0}, // up
    {1, 0},  // down
    {0, -1}, // left
    {0, 1}   // right
};
  public void explodeCheck() {
    // 1. Tick all bombs ONCE
    for (int i = 0; i < bombList.size(); i++) {
        if (bombList.get(i).getFuse() > 0) {
            bombList.get(i).tickFuse();
            //if (bombList.get(i).isReal()) {System.out.println("Real bomb");}
        }
    }
    // 2. Now, repeatedly process all bombs with fuse == 0 (chain reactions)
    boolean exploded;
    do {
        exploded = false;
        for (int i = bombList.size() - 1; i >= 0; i--) {
            if (bombList.get(i).getFuse() == 0) {
                // Run Explosion Process
                BombFireGroup group = new BombFireGroup(fireFuse);
                addBombFireGroup(group);
                Bomb bomb = bombList.get(i);
                for (int[] dir : DIRECTIONS) {
                    blowInDirection(bomb, 1, group, dir[0], dir[1]);
                }
                addBombFire(group, bomb.getRow(), bomb.getCol());
                if (bomb.isReal()) {
                bomb.remove(); // remove the bomb from the player
                }
                this.bombList.remove(i);
                exploded = true;
            }
        }
    } while (exploded);
}
  public void explode(Bomb bomb) {
    //Run Explosion Process
    BombFireGroup group = new BombFireGroup(5);
    addBombFireGroup(group);
    for (int[] dir : DIRECTIONS) {
        blowInDirection(bomb, 1, group, dir[0], dir[1]);
    }
    addBombFire(group, bomb.getRow(), bomb.getCol());
    bombList.remove(bomb); // remove the bomb from the list after explosion
  }
public void blowInDirection(Bomb bomb, int num, BombFireGroup group, int dr, int dc) {
    if (num <= bomb.getPower()) {
        int r = bomb.getRow() + dr * num;
        int c = bomb.getCol() + dc * num;
        if (withinR(r) && withinC(c)) {
            if (field[r][c].getSolid() == false) {
                addBombFire(group, r, c);
                blowInDirection(bomb, num + 1, group, dr, dc);
            } else if (field[r][c] instanceof Bomb) {
                Bomb targetBomb = (Bomb) field[r][c];
                targetBomb.detonate();
            } else if (field[r][c].getBreakable() == true) {
                softWallUpdates.add((SoftWall)getTile(r, c)); // add to soft wall updates
                //System.out.println("Blowing up breakable tile at (" + r + ", " + c + ")");
                //System.out.println(softWallUpdates);
            }
        }
    }
}
// printMap() method for debugging and visual aid
  public void printMap() {
    for (int row = 0; row < field.length; row++) {
      for (int col = 0; col < field[0].length; col++) {
        if (field[row][col] instanceof HardWall) {
          System.out.print("H");
        } else if (field[row][col] instanceof SoftWall) {
          System.out.print("S");
        } else if (field[row][col] instanceof SpawnTile) {
          System.out.print("A");
        }  else if (field[row][col] instanceof Bomb) {
          System.out.print("B");
        } else if (field[row][col] instanceof BombFire) {
          System.out.print("F");
        } else if (field[row][col] instanceof Tile) {
          System.out.print(" ");
        }
        System.out.print(" ");
      }
      System.out.println();
    }
  }
 
  public Map copyWithModifiedFuses(double bombFuseMultiplier, int fireFuseAdd) {
    Map newMap = new Map(this); // Deep copy

    // Multiply all bomb fuses
    for (Bomb bomb : newMap.getBombList()) {
        bomb.setFuse((int)(bomb.getFuse() * bombFuseMultiplier));
    }

    // Add fireFuseAdd to all BombFireGroup fuses
    for (BombFireGroup group : newMap.getBombFireList()) {
        group.setFuse(group.getFuse() + fireFuseAdd);
    }

    return newMap;
}
  public Map mapUpdatedByTicks(int ticks) {
    Map copy = new Map(this); // Deep copy to avoid mutating the original
    copy.mapUpdate(ticks);    // Advance the copy by the given number of ticks
    return copy;
}
}
