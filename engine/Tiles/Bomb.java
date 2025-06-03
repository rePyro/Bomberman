// package declarations and imports here;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Bomb extends Tile {
  // variables
  private int fuse;
  private int row;
  private int col;
  private int power;
  private int tileSize = 48; // Assuming tileSize is 32 pixels, adjust as needed
  
  public Bomb(int row, int col) {
    super("Bomb", true, true);
    this.row = row;
    this.col = col;
    fuse = 180; 
    power = 1;
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
    // If Bomb ever contains mutable objects, clone them here as well.
    return cloned;
}
//updates
  public void tickFuse() {
    fuse--;
  }
  public void detonate() {
    this.fuse = 0;
}
// accessors
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
  //Lazy
  // TODO: make Bomb functions with gameticks
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
    if (fuse <= 60) { image = bomb3; } 
    else if (fuse <= 120) { image = bomb2; }
    else if (fuse <= 180) { image = bomb1; }
    if (image != null) {
      //System.out.println("DrawCheck");
      g2.drawImage(image, col*48, row*48,48, 48, null);
    }
  }
}
