// package declarations and imports here;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

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
  private boolean alive = true; // player is alive by default
  private Tile[][] field; // the field of the map, used for collision detection
  // for sprite rendering
  private String direction = "down";
  private BufferedImage idleU1, idleU2, idleD1, idleD2, idleL1, idleL2, idleR1, idleR2; // idle sprites
  private BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; // movement sprites
  private BufferedImage ded1, ded2, ded3, ded4, ded5, ded6, ded7, ded8, ded9; // death sprites
//constructor
  public Player(Map map, int row, int col) {
    playerCount++;
    playerNumber = playerCount; // assign the current player count to this player
    x = (int)((col+0.5)*48);
    y = (int)((row+0.5)*48);
    this.row = row;
    this.col = col;
    tileSize = 48; // size of the tile in pixels
    speed = 4;
    rowCount = map.getField().length;
    field = map.getField(); // get the field from the map
    //getPlayerImage();
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
    field = map.getField(); // get the field from the map
    //getPlayerImage();
  }
  public void killPlayer() {
    alive = false; // set player to dead
  }
  public void respawn(Map map) {
    alive = true; // set player to alive
    map.setTile(1, 1, new SpawnTile()); // clear the tile where the player was
          row = 1;
          col = 1;
    this.row = row;
    this.col = col;
    x = (int)((col+0.5)*48);
    y = (int)((row+0.5)*48);
  }
  public void getPlayerImage() {
    String spriteName;
    if (playerNumber == 1) {
      spriteName = "boi";
    } else {
      spriteName = "lad";
    }
    try {
      idleU1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-idleU1.png"));
      idleU2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-idleU2.png"));
      idleD1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-idleD1.png"));
      idleD2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-idleD2.png"));
      idleL1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-idleL1.png"));
      idleL2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-idleL2.png"));
      idleR1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-idleR1.png"));
      idleR2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-idleR2.png"));
      up1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-up1.png"));
      up2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-up2.png"));
      down1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-down1.png"));
      down2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-down2.png"));
      left1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-left1.png"));
      left2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-left2.png"));
      right1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-right1.png"));
      right2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-right2.png"));
      ded1 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded1.png"));
      ded2 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded2.png"));
      ded3 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded3.png"));
      ded4 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded4.png"));
      ded5 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded5.png"));
      ded6 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded6.png"));
      ded7 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded7.png"));
      ded8 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded8.png"));
      ded9 = ImageIO.read(getClass().getResourceAsStream("/graphics/PlayerSprites/Bomber" + spriteName + "-ded9.png"));
    } catch (IOException e) {
      e.printStackTrace(); // print the stack trace if there is an error loading the images
      System.out.println("Error loading player images!"); // print an error message
    }
  }
  //accessors
  public int getPlayerNumber() {
    return playerNumber; // return the player number
  }
  public boolean isAlive() {
    return alive; // return the alive status of the player
  }
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
  public String getDirection() {
    return direction;
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
  public void setDirection(String d) {
    this.direction = d;
  }
  //Grid conversion
  public void indexPos() {
    row = (int)(y/48+0.5);
    col = (int)(x/48+0.5);
  }
  public int rowToY() {
    return (int)((row+0.5)*48);
  }
  public int colToX() {
    return (int)((col+0.5)*48);
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
  public boolean checkUp(Map map, int row, int col) {
    if (withinR(row - 1) && withinC(col)) {
      return (map.isFree(row-1, col));
    }
    else {
      return false; // if the row or column is out of bounds, return false
    }
  }
  public boolean checkDown(Map map, int row, int col) {
    if (withinR(row + 1) && withinC(col)) {
      return (map.isFree(row+1, col));
    }
    else {
      return false; // if the row or column is out of bounds, return false
    }
  }
  public boolean checkLeft(Map map, int row, int col) {
    if (withinR(row) && withinC(col - 1)) {
      return (map.isFree(row, col-1));
    }
    else {
      return false; // if the row or column is out of bounds, return false
    }
  }
  public boolean checkRight(Map map, int row, int col) {
    if (withinR(row) && withinC(col + 1)) {
      return (map.isFree(row, col+1));
    }
    else {
      return false; // if the row or column is out of bounds, return false
    }
  }
  //Safety checks
  public boolean checkUpSafe(Map map, int row, int col) {
    if (withinR(row - 1) && withinC(col)) {
      return (map.isFree(row-1, col) && map.getTile(row - 1, col) instanceof BombFire == false);
    }
    else {
      return false; // if the row or column is out of bounds, return false
    }
  }
  public boolean checkDownSafe(Map map, int row, int col) {
    if (withinR(row + 1) && withinC(col)) {
      return (map.isFree(row+1, col) && map.getTile(row + 1, col) instanceof BombFire == false);
    }
    else {
      return false; // if the row or column is out of bounds, return false
    }
  }
  public boolean checkLeftSafe(Map map, int row, int col) {
    if (withinR(row) && withinC(col - 1)) {
      return (map.isFree(row, col-1) && map.getTile(row, col - 1) instanceof BombFire == false);
    }
    else {
      return false; // if the row or column is out of bounds, return false
    }
  }
  public boolean checkRightSafe(Map map, int row, int col) {
    if (withinR(row) && withinC(col + 1)) {
      return (map.isFree(row, col+1) && map.getTile(row, col + 1) instanceof BombFire == false);
    }
    else {
      return false; // if the row or column is out of bounds, return false
    }
  }
  //Movement
  private int tileLeniency = 20;//margin of error for tile movement, so that the player can move even if they are not exactly on the tile
  public boolean canMoveUp(Map map) {
    if (withinR(row)) {
    if (rowToY() < y) {
      return true;
    }
    else if (checkUp(map) == true && Math.abs(colToX() - x) < tileLeniency) {
      x = colToX();
      return true;
    }
    else {
      return false;
    }
    }
    else {
      return false; // if the row is out of bounds, return false
    }
  }
  public boolean canMoveDown(Map map) {
    if (withinR(row)) {
    if (rowToY() > y) {
      return true;
    }
    else if (checkDown(map) == true && Math.abs(colToX() - x) < tileLeniency) {
      x = colToX();
      return true;
    }
    else {
      return false;
    }
    }
    else {
      return false; // if the row is out of bounds, return false
    }
  }
  public boolean canMoveLeft(Map map) {
    if (withinC(col)) {
    if (colToX() < x) {
      return true;
    }
    else if (checkLeft(map) == true &&  Math.abs(rowToY() - y) < tileLeniency) {
      y = rowToY();
      return true;
    }
    else {
      return false;
    }
    }
    else {
      return false; // if the column is out of bounds, return false
    }
  }
  public boolean canMoveRight(Map map) {
    if (withinC(col)) {
    if (colToX() > x) {
      return true;
    }
    if (checkRight(map) == true && Math.abs(rowToY() - y) < tileLeniency) {
      y = rowToY();
      return true;
    }
    else {
      return false;
    }
    }
    else {
      return false; // if the column is out of bounds, return false
    }
  }
  public boolean canMoveUp(Map map, int row, int col) {
    if (checkUp(map, row, col) == true && Math.abs(colToX() - x) < tileLeniency) {
      x = colToX();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveDown(Map map, int row, int col) {
    if (checkDown(map, row, col) == true && Math.abs(colToX() - x) < tileLeniency) {
      x = colToX();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveLeft(Map map, int row, int col) {
    if (checkLeft(map, row, col) == true &&  Math.abs(rowToY() - y) < tileLeniency) {
      y = rowToY();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveRight(Map map, int row, int col) {
    if (checkRight(map, row, col) == true && Math.abs(rowToY() - y) < tileLeniency) {
      y = rowToY();
      return true;
    }
    else {
      return false;
    }
  }
  // Safe movement
  public boolean canMoveUpSafe(Map map, int row, int col) {
    if (checkUpSafe(map, row, col) == true && Math.abs(colToX() - x) < tileLeniency) {
      x = colToX();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveDownSafe(Map map, int row, int col) {
    if (checkDownSafe(map, row, col) == true && Math.abs(colToX() - x) < tileLeniency) {
      x = colToX();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveLeftSafe(Map map, int row, int col) {
    if (checkLeftSafe(map, row, col) == true &&  Math.abs(rowToY() - y) < tileLeniency) {
      y = rowToY();
      return true;
    }
    else {
      return false;
    }
  }
  public boolean canMoveRightSafe(Map map, int row, int col) {
    if (checkRightSafe(map, row, col) == true && Math.abs(rowToY() - y) < tileLeniency) {
      y = rowToY();
      return true;
    }
    else {
      return false;
    }
  }
  //Death Stuff (GAMEPLAY?!)
public void deathCheck(Map map) {
    if (alive == true && map.getTile(row, col).getTileType().equals("BombFire")) { // if the player is on a BombFire tile
      killPlayer(); // kill the player
      System.out.println("Player " + playerNumber + " has died!\nSkill Issue Loser heh"); // print death message
    }
} 
  //Debugging
  public String toString() {
    return "Player " + playerNumber + ": (" + row + ", " + col + ") at (" + x + ", " + y + ")";
  }



}