// imports 
import javax.swing.JFrame;
import java.util.Scanner;
public class Main 
{
  public static void main(String[] args) 
  {
    // create a new game window
    GameWindow window = new GameWindow();
    System.out.println("great success");
    // create a new map object
    Map map = new Map();
    map.printMap();

    GamePanel gamePanel = new GamePanel();
    window.addPanel(gamePanel); // add the game panel to the window
    // create two players
    Player player1 = new Player(map, 1, 1); // player 1 at top left corner
    Player player2 = new Player(map, map.getField().length - 2, map.getField()[0].length - 2); // player 2 at bottom right corner
    gamePanel.getPlayers(player1, player2); // add players to the game panel
    // start the game clock
    gamePanel.startGameThread();

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
}
}   