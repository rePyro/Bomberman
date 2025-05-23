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
    window.setSize(800, 600);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);
    window.setResizable(false);
    window.setLocationRelativeTo(null);
    window.setIconImage(new ImageIcon("graphics/tempIcon.png").getImage());
  }

}
