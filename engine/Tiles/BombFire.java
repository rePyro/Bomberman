import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Graphics2D;
import java.io.File;

public class BombFire extends Tile {
    // package declarations and imports here;

  // variables
  private int fuse;
  int row;
  int col;
  private BombFireGroup group;

public void setGroup(BombFireGroup group) {
    this.group = group;
}

public int getFuse() {
    return group != null ? group.getFuse() : 0;
}
  
  public BombFire(int row, int col) {
    super("BombFire", false, false);
    fuse = 60; 
    this.row = row;
    this.col = col;
    getBombFireImage();
  }
  public BombFire(BombFireGroup group, int row, int col) {
    super("BombFire", false, false);
    fuse = 60; 
    this.row = row;
    this.col = col;
    this.group = group;
    group.addFire(this);
    getBombFireImage();
  }
  // clone
  @Override
public BombFire clone() {
    BombFire cloned = (BombFire) super.clone();
    // Do not clone the group reference here; it will be set by BombFireGroup.clone()
    cloned.fuse = this.fuse;
    cloned.row = this.row;
    cloned.col = this.col;
    cloned.group = null; // Will be set by BombFireGroup.clone()
    return cloned;
  }
  public int getRow() {
    return row;
  }
  public int getCol() {
    return col;
  }
  public BombFireGroup getGroup() {
    return group;
  }

  // rendering
  private BufferedImage explosion1, explosion2, explosion3;

  public void getBombFireImage() {
    try {
      explosion1 = ImageIO.read(new File("graphics/TileSprites/Explosion-1.png"));
      explosion2 = ImageIO.read(new File("graphics/TileSprites/Explosion-2.png"));
      explosion3 = ImageIO.read(new File("graphics/TileSprites/Explosion-3.png"));
    } catch (IOException e) {
      e.printStackTrace(); // print the stack trace if there is an error loading the images
      System.out.println("Error loading player images!"); // print an error message
    }
  }
  
  public void draw(Graphics2D g2) {
    if (this.group == null) {
      //System.out.println("BombFire group is null, cannot draw.");
      return; // If the group is null, we cannot draw the fire
    }
      int fuse = this.group.getFuse();
    //System.out.println("Drawing BombFire at (" + row + ", " + col + ") with fuse: " + fuse);
    BufferedImage image = null; 
    if (fuse <= 12) { image = explosion1; } 
    else if (fuse <= 24) { image = explosion2; } 
    else if (fuse <= 36) { image = explosion3; } 
    else if (fuse <= 48) { image = explosion2; }
    else if (fuse <= 60) { image = explosion1; }
    if (image != null) {
      //System.out.println("DrawCheck");
      g2.drawImage(image, col*48, row*48,48, 48, null);
    }
  }
}
