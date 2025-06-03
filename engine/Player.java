// package declarations and imports here;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics2D;
import java.io.File;

public class Player 
{
  private static int playerCount = 0; // static variable to keep track of players
  private int playerNumber; // instance variable to keep track of this player's number
  private int x;
  private int y;
  private int speed;
  private int maxBombCount = 1;
  private int bombsPlaced = 0; // number of bombs the player can place
  private int bombPower = 1;
  private int row;
  private int col;
  private int rowCount;
  private int tileSize;
  private boolean alive;
  private boolean dying = false; // boolean to check if the player is dying
  private Tile[][] field; // the field of the map, used for collision detection
  // for sprite rendering
  private String direction = "down";
  private BufferedImage idleU1, idleU2, idleD1, idleD2, idleL1, idleL2, idleR1, idleR2; // idle sprites
  private BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; // movement sprites
  private BufferedImage ded1, ded2, ded3, ded4, ded5, ded6, ded7, ded8, ded9; // death sprites

  public Player(Map map, int row, int col, KeyHandler keyHandler) {
    this(map, keyHandler);
    this.row = row; // overwrite the row to the given row
    this.col = col; // overwrite the column to the given column
  }
  //constructor
  public Player(Map map, int row, int col) {
    playerCount++;
    playerNumber = playerCount; // assign the current player count to this player
    x = (int)((col)*48);
    y = (int)((row)*48);
    this.row = row;
    this.col = col;
    tileSize = 48; // size of the tile in pixels
    speed = 1;
    rowCount = map.getField().length;
    field = map.getField(); // get the field from the map
    //getPlayerImage();
    //this.alive = true; // set player to alive
  }
  public Player(Map map, KeyHandler keyHandler) {
    playerCount++;
    this.playerNumber = playerCount; // assign the current player count to this player
    if (playerCount == 1) {
      this.row = 1; // player 1 starts at (1, 1)
      this.col = 1;
    } else {
      this.row = map.getField().length - 2; // player 2 starts at the bottom right corner
      this.col = map.getField()[0].length - 2;
    }
    this.x = (int)(col*48);
    this.y = (int)(row*48);
    this.speed = 1;
    this.rowCount = map.getField().length;
    this.tileSize = 48;
    this.alive = true;
    this.field = map.getField(); 
    this.keyHandler = keyHandler;
    getPlayerImage();
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
    x = (int)((col)*48);
    y = (int)((row)*48);
    rowCount = map.getField().length;
    field = map.getField(); // get the field from the map
    //getPlayerImage();
  }


  public void killPlayer() {
    alive = false; // set player to dead
  }
  public void respawn(Map map) {
    if (alive || dying) {return;}
    alive = true; // set player to alive
    if (playerNumber == 1) {
      map.setTile(1, 1, new Tile()); // clear the tile
      this.row = 1;
      this.col = 1;
      x = (int)((col)*48);
      y = (int)((row)*48);
    } else {
      map.setTile(field.length - 2, field[0].length - 2, new Tile()); // clear the tile
      this.row = field.length - 2;
      this.col = field[0].length - 2;
      x = (int)((col)*48);
      y = (int)((row)*48);
    }
  }
  
  //rendering
  public void getPlayerImage() {
    String spriteName;
    if (playerNumber == 1) {
      spriteName = "boi";
    } else {
      spriteName = "lad";
    }
    try {
      idleU1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-idleU1.png"));
      idleU2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-idleU2.png"));
      idleD1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-idleD1.png"));
      idleD2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-idleD2.png"));
      idleL1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-idleL1.png"));
      idleL2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-idleL2.png"));
      idleR1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-idleR1.png"));
      idleR2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-idleR2.png"));
      up1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-up1.png"));
      up2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-up2.png"));
      down1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-down1.png"));
      down2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-down2.png"));
      left1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-left1.png"));
      left2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-left2.png"));
      right1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-right1.png"));
      right2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-right2.png"));
      ded1 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded1.png"));
      ded2 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded2.png"));
      ded3 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded3.png"));
      ded4 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded4.png"));
      ded5 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded5.png"));
      ded6 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded6.png"));
      ded7 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded7.png"));
      ded8 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded8.png"));
      ded9 = ImageIO.read(new File("graphics/PlayerSprites/Bomber" + spriteName + "-ded9.png"));
    } catch (IOException e) {
      e.printStackTrace(); // print the stack trace if there is an error loading the images
      System.out.println("Error loading player images!"); // print an error message
    }
  }

  private KeyHandler keyHandler;
  private int spriteNumber = 0; // variable to help cycle animations, range 0-3
  private int spriteCounter = 0; // variable incrementing every frame
  public int getSpriteNumber() { return spriteNumber; }
  public int getSpriteCounter() { return spriteCounter; }
  public void setSpriteNumber(int input) { spriteNumber = input; }
  public void setSpriteCounter(int input) { spriteCounter = input; }
  public KeyHandler getKeyHandler() { return keyHandler; }
  public void setKeyHandler(KeyHandler keyHandler) { this.keyHandler = keyHandler; }
 
  public void draw(Graphics2D g2) {
    BufferedImage image = null; // variable to hold the image to be drawn
    if (alive && playerNumber == 1) {
      if (keyHandler.getDownPressed()) {
        direction = "down";
        if (spriteNumber == 0) { image = down1; } 
        else if (spriteNumber == 2) { image = down2; }
        else { image = idleD1; }
      } else if (keyHandler.getUpPressed()) {
        direction = "up";
        if (spriteNumber == 0) { image = up1; } 
        else if (spriteNumber == 2) { image = up2; }
        else { image = idleU1; }
      } else if (keyHandler.getLeftPressed()) {
        direction = "left";
        if (spriteNumber == 0) { image = left1; } 
        else if (spriteNumber == 2) { image = left2; }
        else { image = idleL1; }
      } else if (keyHandler.getRightPressed()) {
        direction = "right";
        if (spriteNumber == 0) { image = right1; } 
        else if (spriteNumber == 2) { image = right2; }
        else { image = idleR1; }
      } else {
        switch (direction) {
          case "up": if (spriteNumber / 2 == 0) { image = idleU1; } else { image = idleU2; } break;
          case "down": if (spriteNumber / 2 == 0) { image = idleD1; } else { image = idleD2; } break;
          case "left": if (spriteNumber / 2 == 0) { image = idleL1; } else { image = idleL2; } break;
          case "right": if (spriteNumber / 2 == 0) { image = idleR1; } else { image = idleR2; } break;
        }
      }
    }
    else if (alive && playerNumber == 2) {
      if (keyHandler.getDownPressed2()) {
        direction = "down";
        if (spriteNumber == 0) { image = down1; } 
        else if (spriteNumber == 2) { image = down2; }
        else { image = idleD1; }
      } else if (keyHandler.getUpPressed2()) {
        direction = "up";
        if (spriteNumber == 0) { image = up1; } 
        else if (spriteNumber == 2) { image = up2; }
        else { image = idleU1; }
      } else if (keyHandler.getLeftPressed2()) {
        direction = "left";
        if (spriteNumber == 0) { image = left1; } 
        else if (spriteNumber == 2) { image = left2; }
        else { image = idleL1; }
      } else if (keyHandler.getRightPressed2()) {
        direction = "right";
        if (spriteNumber == 0) { image = right1; } 
        else if (spriteNumber == 2) { image = right2; }
        else { image = idleR1; }
      } else {
        switch (direction) {
          case "up": if (spriteNumber / 2 == 0) { image = idleU1; } else { image = idleU2; } break;
          case "down": if (spriteNumber / 2 == 0) { image = idleD1; } else { image = idleD2; } break;
          case "left": if (spriteNumber / 2 == 0) { image = idleL1; } else { image = idleL2; } break;
          case "right": if (spriteNumber / 2 == 0) { image = idleR1; } else { image = idleR2; } break;
        }
      }
    }
    else if (dying) { // if the player is dying, draw the death animation
        if (spriteCounter >= 70) {
            image = ded9; // last frame of death animation
        } else if (spriteCounter >= 60) {
            image = ded8;
        } else if (spriteCounter >= 50) {
            image = ded7;
        } else if (spriteCounter >= 40) {
            image = ded6;
        } else if (spriteCounter >= 30) {
            image = ded5;
        } else if (spriteCounter >= 20) {
            image = ded4;
        } else if (spriteCounter >= 10) {
            image = ded3;
        } else if (spriteCounter >= 0) {
            image = ded2;
        } else {
            image = ded1; // first frame of death animation
      }
    }
    // Draw the image if it's loaded
    //System.out.println("nothing detected");
    if (image != null) {
      g2.drawImage(image, x, y, tileSize, tileSize, null);
      //System.out.println("Drawing player" + playerNumber + " (" + x + ", " + y + "), sN " + spriteNumber + ", d: " + direction + ", dP: " + keyHandler.getDownPressed() + ", tS: " + tileSize);
    }
  }
  
  public void updateSpriteVals() {
    if (alive) {
    spriteCounter++;
    if (spriteCounter == 10) { // if 10 frames passed, flip sprite counter
      spriteNumber++;
      spriteCounter = 0;
      if (spriteNumber == 4) { spriteNumber = 0; } // cycle back
    }
  }
  else if (dying) {
    spriteCounter++;
    if (spriteCounter >= 80) {dying = false; spriteCounter = 0; System.out.println("Finished dying");} // reset sprite number and counter
  }
}

  

  //accessors
  public int getPlayerNumber() {
    return playerNumber; // return the player number
  }
  public boolean isAlive() {
    return alive; // return the alive status of the player
  }
  public boolean isDying() {
    return dying; // return the dying status of the player
  }
  public int getPower() {
    return bombPower;
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
  public void setSpeed(int speed) {
    this.speed = speed;
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
    row = (int)((y+24)/48); // set hitbox/magenta square to center of tile
    col = (int)((x+24)/48); // ditto
  }
  public int rowToY() {
    return (int)((row)*48);
  }
  public int colToX() {
    return (int)((col)*48);
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
  private int tileLeniency = 15;//margin of error for tile movement, so that the player can move even if they are not exactly on the tile
  public boolean canMoveUp(Map map) {
    if (withinR(row)) {
        if (y > rowToY()) { // FIXED: was rowToY() < y
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
        if (y < rowToY()) { // FIXED: was rowToY() > y
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
      dying = true; // set the player to dying
      spriteNumber = 0; // reset the sprite number
      System.out.println("Player " + playerNumber + " has died!\nSkill Issue Loser heh"); // print death message
    }
} 
public void upgradeCheck(Map map) {
    if (alive == true && map.getTile(row, col) instanceof CountUpgrade) { // if the player is on a CountUpgrade tile
      maxBombCount++;
      map.setTile(row, col, new Tile(row, col));
      System.out.println("Player " + playerNumber + " has picked up a Count Upgrade!"); // print upgrade message
    }
    else if (alive == true && map.getTile(row, col) instanceof PowerUpgrade) { // if the player is on a PowerUpgrade tile
      bombPower++;
      map.setTile(row, col, new Tile(row, col));
      System.out.println("Player " + playerNumber + " has picked up a Power Upgrade!"); // print upgrade message
    }
    else if (alive == true && map.getTile(row, col) instanceof SpeedUpgrade) { // if the player is on a SpeedUpgrade tile
      speed++;
      map.setTile(row, col, new Tile(row, col));
      System.out.println("Player " + playerNumber + " has picked up a Speed Upgrade!"); // print upgrade message
    }
  }
  public void addBomb() {
    bombsPlaced++;
  }
  public void removeBomb() {
    bombsPlaced--;
  }
  public boolean canAddBomb() {
    return (bombsPlaced < maxBombCount);
  }
  //Debugging
  public String toString() {
    return "Player " + playerNumber + ": (" + row + ", " + col + ") at (" + x + ", " + y + ")";
  }



}