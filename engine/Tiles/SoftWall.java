// package declarations and imports here;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SoftWall extends Tile
{
  private int currentFrame = 60; // max is 60, down to 0


  public SoftWall() {
    super("SoftWall", true, true);
    getSoftWallImage();
  }
  
  public SoftWall(int row, int col) {
    super("SoftWall", true, true);
    setRowIndex(row); setColIndex(col);
    getSoftWallImage();
  }
  public void update() {
    currentFrame--;
  }

  // rendering
  private BufferedImage softWall1, softWall2, softWall3, softWall4, softWall5, softWall6;

  public void getSoftWallImage() {
    try {
      softWall1 = ImageIO.read(new File("graphics/TileSprites/SoftWall-1.png"));
      softWall2 = ImageIO.read(new File("graphics/TileSprites/SoftWall-2.png"));
      softWall3 = ImageIO.read(new File("graphics/TileSprites/SoftWall-3.png"));
      softWall4 = ImageIO.read(new File("graphics/TileSprites/SoftWall-4.png"));
      softWall5 = ImageIO.read(new File("graphics/TileSprites/SoftWall-5.png"));
      softWall6 = ImageIO.read(new File("graphics/TileSprites/SoftWall-6.png"));
    } catch (IOException e) {
      e.printStackTrace(); // print the stack trace if there is an error loading the images
      System.out.println("Error loading player images!"); // print an error message
    }
  }
  
  public void draw(Graphics2D g2) {
    if (currentFrame < 60) {
      //System.out.println("Drawing SoftWall at (" + this.getRowIndex() + ", " + this.getColIndex() + ") with state: " + currentFrame);
       // Do not draw if the wall is destroyed
    }
    
    BufferedImage image = null; 
    if (currentFrame <= 12) { image = softWall6; } 
    else if (currentFrame <= 24) { image = softWall5; } 
    else if (currentFrame <= 36) { image = softWall4; } 
    else if (currentFrame <= 48) { image = softWall3; }
    else if (currentFrame <= 59) { image = softWall2; }
    else if (currentFrame <= 60) { image = softWall1;}
    if (image != null) {
      //System.out.println("DrawCheck");
      g2.drawImage(image, this.getColIndex()*48, this.getRowIndex()*48,48, 48, null);
    }
  }

  public int getCurrentFrame() {
    return currentFrame;
  }
}
