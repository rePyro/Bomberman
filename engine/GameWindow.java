// package declarations and imports here;
import javax.swing.JFrame;
import javax.swing.ImageIcon;


public class GameWindow 
{
  // class code here;
  JFrame window = new JFrame();

  // constructor
  public GameWindow() {
    window.setTitle("Bomberman");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);
    window.setResizable(false);
    window.setLocationRelativeTo(null);
    window.setIconImage(new ImageIcon("graphics/tempIcon.png").getImage());
  }
  
  // methods
  public void addPanel(GamePanel gamePanel) {
    window.add(gamePanel);
    window.pack(); // pack the window to fit the panel
  }
}
