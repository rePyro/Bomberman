import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.GraphicsConfigTemplate;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TileImageStorage {
  // variables
  private BufferedImage bomb1, bomb2, bomb3;
  private BufferedImage countUpgrade1, countUpgrade2, countUpgrade3, countUpgrade4, countUpgrade5;
  private BufferedImage explosion1, explosion2, explosion3;
  private BufferedImage hardWall;
  private BufferedImage powerUpgrade1, powerUpgrade2, powerUpgrade3, powerUpgrade4, powerUpgrade5;
  private BufferedImage softWall1, softWall2, softWall3, softWall4, softWall5, softWall6;
  private BufferedImage speedUpgrade1, speedUpgrade2, speedUpgrade3, speedUpgrade4, speedUpgrade5;
  private BufferedImage tile;
  private BufferedImage upgradeBase1, upgradeBase2, upgradeBase3, upgradeBase4, upgradeBase5;

  private Graphics2D g2;
  private String tileType;
  private int x;
  private int y; 
  private int tileSize; 

  public TileImageStorage() {
    try {
      bomb1         = ImageIO.read(new File("graphics/TileSprites/Bomb-1.png"        ));
      bomb2         = ImageIO.read(new File("graphics/TileSprites/Bomb-2.png"        ));
      bomb3         = ImageIO.read(new File("graphics/TileSprites/Bomb-3.png"        ));
      countUpgrade1 = ImageIO.read(new File("graphics/TileSprites/CountUpgrade-1.png"));
      countUpgrade2 = ImageIO.read(new File("graphics/TileSprites/CountUpgrade-2.png"));
      countUpgrade3 = ImageIO.read(new File("graphics/TileSprites/CountUpgrade-3.png"));
      countUpgrade4 = ImageIO.read(new File("graphics/TileSprites/CountUpgrade-4.png"));
      countUpgrade5 = ImageIO.read(new File("graphics/TileSprites/CountUpgrade-5.png"));
      explosion1    = ImageIO.read(new File("graphics/TileSprites/Explosion-1.png"   ));
      explosion2    = ImageIO.read(new File("graphics/TileSprites/Explosion-2.png"   ));
      explosion3    = ImageIO.read(new File("graphics/TileSprites/Explosion-3.png"   ));
      hardWall      = ImageIO.read(new File("graphics/TileSprites/HardWall.png"      ));
      powerUpgrade1 = ImageIO.read(new File("graphics/TileSprites/PowerUpgrade-1.png"));
      powerUpgrade2 = ImageIO.read(new File("graphics/TileSprites/PowerUpgrade-2.png"));
      powerUpgrade3 = ImageIO.read(new File("graphics/TileSprites/PowerUpgrade-3.png"));
      powerUpgrade4 = ImageIO.read(new File("graphics/TileSprites/PowerUpgrade-4.png"));
      powerUpgrade5 = ImageIO.read(new File("graphics/TileSprites/PowerUpgrade-5.png"));
      softWall1     = ImageIO.read(new File("graphics/TileSprites/SoftWall-1.png"    ));
      softWall2     = ImageIO.read(new File("graphics/TileSprites/SoftWall-2.png"    ));
      softWall3     = ImageIO.read(new File("graphics/TileSprites/SoftWall-3.png"    ));
      softWall4     = ImageIO.read(new File("graphics/TileSprites/SoftWall-4.png"    ));
      softWall5     = ImageIO.read(new File("graphics/TileSprites/SoftWall-5.png"    ));
      softWall6     = ImageIO.read(new File("graphics/TileSprites/SoftWall-6.png"    ));
      speedUpgrade1 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-1.png"));
      speedUpgrade2 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-2.png"));
      speedUpgrade3 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-3.png"));
      speedUpgrade4 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-4.png"));
      speedUpgrade5 = ImageIO.read(new File("graphics/TileSprites/SpeedUpgrade-5.png"));
      tile          = ImageIO.read(new File("graphics/TileSprites/Tile.png"          ));
      upgradeBase1  = ImageIO.read(new File("graphics/TileSprites/UpgradeBase-1.png" ));
      upgradeBase2  = ImageIO.read(new File("graphics/TileSprites/UpgradeBase-2.png" ));
      upgradeBase3  = ImageIO.read(new File("graphics/TileSprites/UpgradeBase-3.png" ));
      upgradeBase4  = ImageIO.read(new File("graphics/TileSprites/UpgradeBase-4.png" ));
      upgradeBase5  = ImageIO.read(new File("graphics/TileSprites/UpgradeBase-5.png" ));
    } catch (IOException e) {
      e.printStackTrace(); // print the stack trace if there is an error loading the images
      System.out.println("Error loading player images!"); // print an error message
    }
  }
  
  public void draw(Graphics2D g2, String tileType, int x, int y, int tileSize) {
    this.g2 = g2;
    this.tileType = tileType;
    this.x = x;
    this.y = y;
    this.tileSize = tileSize;

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