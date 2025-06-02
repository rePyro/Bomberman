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

    // create a new game window w/ map and players
    GameWindow window = new GameWindow();
    GamePanel gamePanel = new GamePanel(map);
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