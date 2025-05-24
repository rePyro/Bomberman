import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    // variables
    private boolean upPressed, downPressed, leftPressed, rightPressed; // movement keys
    private boolean enterPressed, spacePressed; // action keys

    @Override
    public void keyTyped(KeyEvent e) {} // purposely empty, not used
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode(); // get the key code (letter) of the pressed key

        if (key == KeyEvent.VK_W) { upPressed = true; }
        if (key == KeyEvent.VK_A) { leftPressed = true; }
        if (key == KeyEvent.VK_S) { downPressed = true; }
        if (key == KeyEvent.VK_D) { rightPressed = true; }
        if (key == KeyEvent.VK_ENTER) { enterPressed = true; }
        if (key == KeyEvent.VK_SPACE) { spacePressed = true; }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode(); // get the key code (letter) of the released key

        if (key == KeyEvent.VK_W) { upPressed = false; }
        if (key == KeyEvent.VK_A) { leftPressed = true; }
        if (key == KeyEvent.VK_S) { downPressed = true; }
        if (key == KeyEvent.VK_D) { rightPressed = false; }
        if (key == KeyEvent.VK_ENTER) { enterPressed = false; }
        if (key == KeyEvent.VK_SPACE) { spacePressed = false; }
    }

    // accessors
    public boolean getUpPressed() { return upPressed; }
    public boolean getDownPressed() { return downPressed; }
    public boolean getLeftPressed() { return leftPressed; }
    public boolean getRightPressed() { return rightPressed; }
    public boolean getEnterPressed() { return enterPressed; }
    public boolean getSpacePressed() { return spacePressed; }
}
