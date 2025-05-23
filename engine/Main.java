// package declarations and imports here;
public class Main 
{
  // variable and subroutine declarations here;
  public static void main(String[] args) 
  {
    // create a new game window
    GameWindow window = new GameWindow();
    System.out.println("great success");
    // create a new map object
    Map map = new Map();
    Tile[][] testing = map.getField();
    for (int row = 0; row < map.getField().length; row++) {
      for (int col = 0; col < map.getField()[0].length; col++) {
        if (testing[row][col] instanceof HardWall) {
          System.out.print("H");
        } else if (testing[row][col] instanceof SoftWall) {
          System.out.print("S");
        } else {
          System.out.print(" ");
        }
        System.out.print(" ");
        }
        System.out.println();
      }
    }
  }   