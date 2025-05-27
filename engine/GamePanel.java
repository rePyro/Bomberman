// imports
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable {
  // variables
  private final int originalTileSize = 16; // original tile size
  private final int scale = 3; // scale factor, 48 pixels

  private final int tileSize = originalTileSize * scale; // scaled tile size
  private final int maxScreenCol = 15; 
  private final int maxScreenRow = 9; 
  private final int screenWidth = tileSize * maxScreenCol; // 720 pixels
  private final int screenHeight = tileSize * maxScreenRow; // 432 pixels
  
  private int FPS = 60; // frames per second
  private Player player1;
  private Player player2;
  
  KeyHandler keyHandler = new KeyHandler(); // key handler for input
  Thread gameThread; // thread for the game loop

  // constructor
  public GamePanel() {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.black);
    this.setDoubleBuffered(true); // double buffering for smoother graphics
    this.addKeyListener(keyHandler);
    this.setFocusable(true); // requires focus to receive key events
  }

  // methods
  public void getPlayers(Player player1, Player player2) {
    this.player1 = player1;
    this.player2 = player2;
  }

  
  
  public void startGameThread() {
    gameThread = new Thread(this); // create a new thread
    gameThread.start(); // start the thread
  }

  // game loop
  @Override
  public void run() {
    
    // using delta method of rendering. Keeps track of actual FPS. 

    double drawInterval = 1000000000 / FPS; // equivalent to 1 second / FPS, or 0.01666 sec
    double delta = 0;
    long lastTime = System.nanoTime();
    long currentTime;
    long timer = 0;
    long drawCount = 0;

    while (gameThread != null) {
      currentTime = System.nanoTime();
      delta += (currentTime - lastTime) / drawInterval;
      timer += (currentTime - lastTime);
      lastTime = currentTime;
      if (delta >= 1) { // if enough time has passed
        // UPDATE: update information, ex. player position, map, etc.
        update(); 

        // DRAW: paint the new screen
        repaint();
        delta--;
        drawCount++;
        //System.out.println("test: upd + rep'd");
      }

      if (timer >= 1000000000) { // if 1 second has passed
        //System.out.println("FPS: " + drawCount); // print the FPS
        drawCount = 0; // reset the draw count
        timer = 0; // reset the timer
      }
    }
  }

                                                                                      // temp
                                                                                    private int playerX = 100;
                                                                                    private int playerY = 100;
                                                                                    private int playerSpeed = 4;

  public void update() {
    //keyHandler.setLeftPressed(true);
    if (keyHandler.getUpPressed() == true) { // if the up key is pressed
      playerY -= playerSpeed;
      System.out.println("test: moving up");
    } else if (keyHandler.getDownPressed() == true) { // if the down key is pressed
      playerY += playerSpeed;
      System.out.println("test: moving down");
    } else if (keyHandler.getRightPressed() == true) { // if the right key is pressed
      playerX -= playerSpeed;
      System.out.println("test: moving right");
    } else if (keyHandler.getLeftPressed() == true) { // if the left key is pressed
      playerX += playerSpeed;
      System.out.println("test: moving left");
    } else {
      System.out.println("test: no movement");
    }
  }

  public void paintComponent(Graphics g) { 
    super.paintComponent(g);                       // basically, this enables repaint() to work
    Graphics2D g2 = (Graphics2D)g;                 // cast g to Graphics2D for more functions
    g2.setColor(Color.white);
    g2.fillRect(playerX, playerY, tileSize, tileSize); // fill the square with white
    g2.dispose();                                  // saves memory
  } 
}