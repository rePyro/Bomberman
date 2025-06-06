import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TileImageStorage {
  // variables
  private BufferedImage bomb2, countUpgrade1, explosion1, 
  hardWall, powerUpgrade1, softWall1, speedUpgrade1, tile;

  public TileImageStorage() {
    try {
      bomb2         = ImageIO.read(new File("graphics/TileSprites/Bomb-2.png"        ));
      countUpgrade1 = ImageIO.read(new File("graphics/TileSprites/CountUpgrade-1.png"));
      explosion1    = ImageIO.read(new File("graphics/TileSprites/Explosion-1.png"   ));
      hardWall      = ImageIO.read(new File("graphics/TileSprites/HardWall.png"      ));
      powerUpgrade1 = ImageIO.read(new File("graphics/TileSprites/PowerUpgrade-1.png"));
      softWall1     = ImageIO.read(new File("graphics/TileSprites/SoftWall-1.png"    ));
      speedUpgrade1 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-1.png"));
      tile          = ImageIO.read(new File("graphics/TileSprites/Tile.png"          ));
    } catch (IOException e) {
      e.printStackTrace(); // print the stack trace if there is an error loading the images
      System.out.println("Error loading player images!"); // print an error message
    }
  }
  
  public void draw(Graphics2D g2, String tileType, int x, int y, int tileSize) {
    BufferedImage image = null; // variable to hold the image to be drawn

    switch (tileType) {
      case "Bomb": image = bomb2; break;
      case "CountUpgrade": image = countUpgrade1; break;
      case "Explosion": image = explosion1; break;
      case "HardWall": image = hardWall; break;
      case "PowerUpgrade": image = powerUpgrade1; break;
      case "SoftWall": image = softWall1; break;
      case "SpeedUpgrade": image = speedUpgrade1; break;
      default: image = tile; break;
    }

    if (image != null) {
      g2.drawImage(image, x, y, tileSize, tileSize, null);
    }
  }
}