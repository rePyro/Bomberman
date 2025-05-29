// imports 
import javax.swing.JFrame;
import java.util.Scanner;
public class Main 
{
  public static void main(String[] args) 
  {
    
    System.out.println("great success");
    // create a new map object
    Map map = new Map();
    map.printMap();


    // create two players
    Player player1 = new Player(map); // player 1 at top left corner
    Player player2 = new Player(map); // player 2 at bottom right corner

    // create a new game window
    GameWindow window = new GameWindow();
    GamePanel gamePanel = new GamePanel(map, player1, player2);
    window.addPanel(gamePanel); // add the game panel to the window

    gamePanel.requestFocusInWindow();
    // start the game clock
    gamePanel.startGameThread();

    /* test player spawn and position

    // create a scanner for user input
    Scanner scan = new Scanner(System.in);
    while (true) {
      Player tester = new Player(map, 0, 0);
    System.out.println("X position:");
    int x = scan.nextInt();
    tester.setX(x);
    System.out.println("Y position:");
    int y = scan.nextInt();
    tester.setY(y);
    
    System.out.println("index: ["+tester.getRow()+"], ["+tester.getCol()+"]");
  }
  */
}
}   