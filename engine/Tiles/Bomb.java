// package declarations and imports here;

import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Bomb extends Tile {
  // variables
  private Player player; // ID of the player who placed the bomb
  private int fuse = 180;
  private int row;
  private int col;
  private int power;
  private int tileSize = 48; // Assuming tileSize is 32 pixels, adjust as needed
  private int currentFrame = 0;
  private int currentCase = 0;
  private boolean real;
  
  public Bomb(int row, int col) {
    super("Bomb", true, true);
    this.row = row;
    this.col = col;
    fuse = 180; 
    power = 1;
    real = true;
    getBombImage();
  }
  public Bomb(Player player) {
    super("Bomb", true, true);
    row = player.getRow();
    col = player.getCol();
    this.player = player;
    this.power = player.getPower();
    player.addBomb();
    real = true;
    getBombImage();
  }
  public Bomb(Player player, boolean real) {
    super("Bomb", true, true);
    row = player.getRow();
    col = player.getCol();
    this.player = player;
    this.power = player.getPower();
    if (real) {player.addBomb();}
    this.real = real;
    getBombImage();
  }
  //clone
  @Override
public Bomb clone() {
    Bomb cloned = (Bomb) super.clone();
    // Copy primitive fields (already done by super.clone()), but do it explicitly if needed:
    cloned.fuse = this.fuse;
    cloned.row = this.row;
    cloned.col = this.col;
    cloned.power = this.power;
    cloned.real = false;
    // If Bomb ever contains mutable objects, clone them here as well.
    return cloned;
}
//updates
  public void tickFuse() {
    fuse--;
    currentFrame++;
    if (currentFrame == 10) {
      currentFrame = 0;
      currentCase++;
      if (currentCase > 3) {
        currentCase = 0;
      }
    }
  
  }
  public void detonate() {
    this.fuse = 0;
}
  public void remove() {
    if (this.player != null) {
    player.removeBomb();
    System.out.println("Bomb removed from player: " + player.getPlayerNumber());
  }
  }
// accessors
  public Player getPlayer() {
    return player;
  }
  public boolean isReal() {
    return real;
  }
  public int getFuse() {
    return fuse;
  }
  public int getPower() {
    return power;
  }
  public int getRow() {
    return row;
  }
  public int getCol() {
    return col;
  }
  public int getY() {
    return row * tileSize;
  }
  public int getX() {
    return col * tileSize;
  }
  public boolean tryBreak(Tile[][] field, int row, int col) {
    try {
      if (field[row][col].getBreakable()) {
        field[row][col] = new Tile();
        return true;
      } else {
        return false;
      }
    }  
    catch (ArrayIndexOutOfBoundsException e) {
      return false;
    }
  }
  public void explode(Tile[][] field) {
    for (int r = 1; r <= power; r++)
    {if (tryBreak(field, row +r, col)) {
    }}

  }
  public void setFuse(int fuse) {
    this.fuse = fuse;
  }
  // rendering
  private BufferedImage bomb1, bomb2, bomb3;

  public void getBombImage() {
    try {
      bomb1 = ImageIO.read(new File("graphics/TileSprites/Bomb-1.png"));
      bomb2 = ImageIO.read(new File("graphics/TileSprites/Bomb-2.png"));
      bomb3 = ImageIO.read(new File("graphics/TileSprites/Bomb-3.png"));
    } catch (IOException e) {
      e.printStackTrace(); // print the stack trace if there is an error loading the images
      System.out.println("Error loading player images!"); // print an error message
    }
  }
  
  public void draw(Graphics2D g2) {
    //System.out.println("Drawing BombFire at (" + row + ", " + col + ") with fuse: " + fuse);

    BufferedImage image = null;
    switch (currentCase) {
      case 0: image = bomb3; break;
      case 1: image = bomb2; break;
      case 2: image = bomb1; break;
      case 3: image = bomb2; break; // Loop back to the first frame
      default: image = bomb3; break; // Default to the first frame if out of bounds
    }
    if (image != null) {
      //System.out.println("DrawCheck");
      g2.drawImage(image, col*48, row*48,48, 48, null);
    }
  }


}
