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
    window.setIconImage(new ImageIcon("graphics/BombermanIcon.png").getImage());
  }
  
  // methods
  public void addPanel(GamePanel gamePanel) {
    window.add(gamePanel);
    //window.setSize(gamePanel.getPreferredSize());
    //window.setSize(768, 480);
    window.pack(); // pack the window to fit the panel
        window.setLocationRelativeTo(null);
  }
}
