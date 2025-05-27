// package declarations and imports here;
public class Player 
{
  private static int playerCount = 0; // static variable to keep track of players
  private int playerNumber; // instance variable to keep track of this player's number
  private int x;
  private int y;
  private int speed;
  private int row;
  private int col;
  private int rowCount;
  private int tileSize;

//constructor
  public Player(Map map, int row, int col) {playerCount++;
    playerNumber = playerCount; // assign the current player count to this player
    x = (int)((col+0.5)*48);
    y = (int)((row+0.5)*48);
    speed = 4;
    rowCount = map.getField().length;
    
  }
  public Player(Map map) {
    playerCount++;
    playerNumber = playerCount; // assign the current player count to this player
    for (int i = 0; i < map.getField().length; i++) {
      for (int j = 0; j < map.getField()[i].length; j++) {
        if (map.getField()[i][j].getTileType().equals("SpawnTile") &&
        ((SpawnTile)(map.getField()[i][j])).getSpawnNumber() == playerNumber) { // find the first empty tile
          row = i;
          col = j;
          break;
        }
      }
    }
    this.row = row;
    this.col = col;
    x = (int)((col+0.5)*48);
    y = (int)((row+0.5)*48);
    speed = 4;
    rowCount = map.getField().length;
  }
  //accessors
  public int getX() {
    return x;
  }
  public int getY() {
    return y;
  }
  public int getSpeed() {
    return speed;
  }
  public int getRow() {
    return row;
  }
  public int getCol() {
    return col;
  }
  //mutators
  public void setCol(int col) {
    this.col = col;
    x = colToX();
  }
  public void setRow(int row) {
    this.row = row;
    y = rowToY();
  }
  public void setY(int y) {
    this.y = y;
    indexPos();
  }
  public void setX(int x) {
    this.x = x;
    indexPos();
  }
  //Grid conversion
  public void indexPos() {
    row = (int)(y/48+0.5);
    col = (int)(x/48+0.5);
  }
  public int rowToY() {
    return (int)(row+0.5)*48;
  }
  public int colToX() {
    return (int)(col+0.5)*48;
  }
  //Collision detection
  public boolean checkUp(Map map) {
    return map.isFree(row-1, col);
  }
  public boolean checkDown(Map map) {
    return map.isFree(row+1, col);
  }
  public boolean checkLeft(Map map) {
    return map.isFree(row, col-1);
  }
  public boolean checkRight(Map map) {
    return map.isFree(row, col+1);
  }
  //Movement
  public boolean canMoveUp(Map map) {
    if (checkUp(map) == true && Math.abs(colToX() - x) < 9) {
      x = colToX();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveDown(Map map) {
    if (checkDown(map) == true && Math.abs(colToX() - x) < 9) {
      x = colToX();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveLeft(Map map) {
    if (checkLeft(map) == true &&  Math.abs(rowToY() - y) < 9) {
      y = rowToY();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveRight(Map map) {
    if (checkRight(map) == true && Math.abs(rowToY() - y) < 9) {
      y = rowToY();
      return true;
    }
    else {
      return false;
    }
  }
  



}

// 
  // if player.input.up() == true && grid.isOpen() == true
    // if x within 0.1 of int grid value
      // x = grid value
    // else 
      // NO movement >:c



 //Can go 'up'
 //Check if grid index[r-1][c]=0
 //if true then check if index[r-1][c-1]=0,