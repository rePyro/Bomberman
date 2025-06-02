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

  public final int tileSize = originalTileSize * scale; // scaled tile size
  private final int maxScreenCol = 15; 
  private final int maxScreenRow = 9; 
  private final int screenWidth = tileSize * maxScreenCol; // 720 pixels
  private final int screenHeight = tileSize * maxScreenRow; // 432 pixels
  
  private int FPS = 60; // frames per second
  private Player player1;
  private Player player2;
  
  private Map map; // map object
  private TileImageStorage tileImageStorage = new TileImageStorage(); // tile image storage object
  private Enemy enemy1;
  private SuperMap superMap; // super map object, for future use
  
  KeyHandler keyHandler = new KeyHandler(); // key handler for input
  Thread gameThread; // thread for the game loop

  // constructor
  public GamePanel(Map map) {
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setBackground(Color.black);
    this.setDoubleBuffered(true); // double buffering for smoother graphics
    this.addKeyListener(keyHandler);
    this.setFocusable(true); // requires focus to receive key events
    //this.requestFocusInWindow();
    
    this.map = map;
    this.player1 = new Player(map, keyHandler);
    this.player2 = new Player(map, keyHandler);

    this.enemy1 = new Enemy(map, 1, 1);
    this.superMap = new SuperMap(map); // create a new super map object
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
        map.gameTick();
        enemy1.enemyTick(); // update enemy position (Needs to be done before map updates)
        map.gameTick();
 
        player1.updateSpriteVals();
        player2.updateSpriteVals();

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

  public void update() {
    player1.deathCheck(map); // check if player 1 is dead
    player2.deathCheck(map); // check if player 2 is dead
    //enemy1.deathCheck(map); // check if enemy 1 is dead
    //keyHandler.setLeftPressed(true);
    
    if (keyHandler.getSpaceJustPressed()) {
      System.out.println("test: space just pressed");
      if (player1.isAlive() == false) { // if player is dead
        player1.respawn(map); // respawn the player
        System.out.println("Respawned Player 1");
      } else {
        System.out.println("Player 1 is alive, cannot respawn");
      }
      if (player2.isAlive() == false) { // if player is dead
        player2.respawn(map); // respawn the player
        System.out.println("Respawned Player 2");
      } else {
        System.out.println("Player 2 is alive, cannot respawn");
      }
      keyHandler.resetSpaceJustPressed();
    }
    if (keyHandler.getPJustPressed()) {
      System.out.println("test: p just pressed");
      superMap.makePrediction(); // make a prediction of the map
      superMap.printPrediction(); // print the prediction
      keyHandler.resetPJustPressed();
    }
    //Actions that require life
    if (keyHandler.getEJustPressed()) {
    if (player1.isAlive()) {
      map.addBomb(player1.getRow(), player1.getCol());
      System.out.println("SpawnBomb (if bomb aready there, not re-spawned)");
    } else {System.out.println("heh ded men no get bomb (p1)");}
      keyHandler.resetEJustPressed();
    }
    if (keyHandler.getEnterJustPressed()) {
      if (player2.isAlive()) {
      map.addBomb(player2.getRow(), player2.getCol());
      System.out.println("SpawnBomb (if bomb aready there, not re-spawned)");
    } else {System.out.println("heh ded men no get bomb (p2)");}
      keyHandler.resetEnterJustPressed();
    }
    
    if (player1.isAlive()) { // if player 1 is alive
    if (keyHandler.getUpPressed() == true && player1.canMoveUp(map) == true) { // if the up key is pressed
      player1.setY(player1.getY() - player1.getSpeed());
      //System.out.println("test: moving up");
    } if (keyHandler.getDownPressed() == true && player1.canMoveDown(map) == true) { // if the down key is pressed
      player1.setY(player1.getY() + player1.getSpeed());
      //System.out.println("test: moving down");
    } if (keyHandler.getRightPressed() == true && player1.canMoveRight(map) == true) { // if the right key is pressed
      player1.setX(player1.getX() + player1.getSpeed());
      //System.out.println("test: moving right");
    } if (keyHandler.getLeftPressed() == true && player1.canMoveLeft(map) == true) { // if the left key is pressed
      player1.setX(player1.getX() - player1.getSpeed());
      //System.out.println("test: moving left");
    } //else {
      //System.out.println("test: no movement");
    //}
  }
  
  if (player2.isAlive()) {
    if (keyHandler.getUpPressed2() == true && player2.canMoveUp(map) == true) { // if the up key is pressed
      player2.setY(player2.getY() - player2.getSpeed());
      //System.out.println("test: moving up");
    } if (keyHandler.getDownPressed2() == true && player2.canMoveDown(map) == true) { // if the down key is pressed
      player2.setY(player2.getY() + player2.getSpeed());
      //System.out.println("test: moving down");
    } if (keyHandler.getRightPressed2() == true && player2.canMoveRight(map) == true) { // if the right key is pressed
      player2.setX(player2.getX() + player2.getSpeed());
      //System.out.println("test: moving right");
    } if (keyHandler.getLeftPressed2() == true && player2.canMoveLeft(map) == true) { // if the left key is pressed
      player2.setX(player2.getX() - player2.getSpeed());
      //System.out.println("test: moving left");
    } //else {
      //System.out.println("test: no movement");
    //}
  }
}

  public void paintComponent(Graphics g) { 
    super.paintComponent(g);                       // basically, this enables repaint() to work
    Graphics2D g2 = (Graphics2D)g;                 // cast g to Graphics2D for more functions
    
    for (int row = 0; row < map.getField().length; row++) {
      for (int col = 0; col < map.getField()[0].length; col++) {
        if (map.getField()[row][col].getTileType().equals("HardWall")) {
          tileImageStorage.draw(g2, "HardWall", map.colToX(col), map.rowToY(row), tileSize);
        } else if (map.getField()[row][col] instanceof SoftWall) {
          tileImageStorage.draw(g2, "SoftWall", map.colToX(col), map.rowToY(row), tileSize);
        } else if (map.getField()[row][col] instanceof SpawnTile) {
          //tileImageStorage.draw(g2, "SpawnTile", map.colToX(col), map.rowToY(row), tileSize);
        } else if (map.getField()[row][col] instanceof Bomb) {
          tileImageStorage.draw(g2, "Bomb", map.colToX(col), map.rowToY(row), tileSize);
        } else if (map.getField()[row][col] instanceof BombFire) {
          tileImageStorage.draw(g2, "Explosion", map.colToX(col), map.rowToY(row), tileSize);
        } else if (map.getField()[row][col] instanceof CountUpgrade) {
          tileImageStorage.draw(g2, "CountUpgrade", map.colToX(col), map.rowToY(row), tileSize);
        } else if (map.getField()[row][col] instanceof PowerUpgrade) {
          tileImageStorage.draw(g2, "PowerUpgrade", map.colToX(col), map.rowToY(row), tileSize);
        } else if (map.getField()[row][col] instanceof SpeedUpgrade) {
          tileImageStorage.draw(g2, "SpeedUpgrade", map.colToX(col), map.rowToY(row), tileSize);    
        } else {
          tileImageStorage.draw(g2, "Tile", map.colToX(col), map.rowToY(row), tileSize);
        }
      }
    }
    if (player1.isAlive()) { // if player 1 is alive
    // g2.setColor(Color.magenta);
    // g2.fillRect(player1.colToX(),player1.rowToY(), tileSize, tileSize);
    player1.draw(g2);
    
    // g2.setColor(Color.pink);
    // g2.fillRect(player1.getX(), player1.getY(), tileSize, tileSize); // fill the square with white                              
    } 
    if (player2.isAlive()) { // if player 2 is alive
      // g2.setColor(Color.magenta);
      // g2.fillRect(player2.colToX(),player2.rowToY(), tileSize, tileSize);
      player2.draw(g2);                           
    } 
    //if (enemy1.isAlive()) {
    //  g2.setColor(Color.magenta);
    //  g2.fillRect(enemy1.colToX(),enemy1.rowToY(), tileSize, tileSize);
    //  g2.setColor(Color.lightGray);
    //  g2.fillRect(enemy1.getX(), enemy1.getY(), tileSize, tileSize); // fill the square with white          
    //}
    g2.dispose(); 
  }
}