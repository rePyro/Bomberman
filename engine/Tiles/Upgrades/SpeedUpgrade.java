// package declarations and imports here;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class SpeedUpgrade extends UpgradeBase
{
  // class code here;
  public SpeedUpgrade() {
    super("SpeedUpgrade");
    getUpgradeImage(); // Load images when the object is created
  }

  //Graphics rendering
  private int currentFrame = 0; // Frame counter for animation
  private int currentCase = 0; // Current case for animation frames
  private BufferedImage upgrade1, upgrade2, upgrade3, upgrade4, upgrade5;

  public void getUpgradeImage() {
    try {
      upgrade1 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-1.png"));
      upgrade2 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-2.png"));
      upgrade3 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-3.png"));
      upgrade4 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-4.png"));
      upgrade5 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-5.png"));
    } catch (IOException e) {
      e.printStackTrace(); // print the stack trace if there is an error loading the images
      System.out.println("Error loading SpeedUpgrade images!"); // print an error message
    }
  }
  
  public void draw(Graphics2D g2) {
    //System.out.println("Drawing BombFire at (" + row + ", " + col + ") with fuse: " + fuse);
    currentFrame++;
    if (currentFrame == 10) {
      currentFrame = 0;
      currentCase++;
      if (currentCase > 4) {
        currentCase = 0;
      }
    }
    BufferedImage image = null;
    switch (currentCase) {
      case 0: image = upgrade1; break;
      case 1: image = upgrade2; break;
      case 2: image = upgrade3; break;
      case 3: image = upgrade4; break; 
      case 4: image = upgrade5; break;
      // Loop back to the first frame
      default: image = upgrade1; break; // Default to the first frame if out of bounds
    }
    if (image != null) {
      //System.out.println("DrawCheck");
      g2.drawImage(image, this.getColIndex()*48, this.getRowIndex()*48,48, 48, null);
    }
  }
}