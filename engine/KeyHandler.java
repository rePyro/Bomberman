import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
//TODO: fix this. Issue occurs because key presses aren't being registered, but everything else, like booleans and rendering, works. debug!!
public class KeyHandler implements KeyListener {
    // variables
    private boolean upPressed, downPressed, leftPressed, rightPressed; // movement keys
    private boolean enterPressed, spacePressed; // action keys
    private boolean enterJustPressed = false;

    @Override
    public void keyTyped(KeyEvent e) {} // purposely empty, not used
    
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // get the key code (letter) of the pressed key
        
        if (code == KeyEvent.VK_W) { upPressed = true; }
        if (code == KeyEvent.VK_A) { leftPressed = true; }
        if (code == KeyEvent.VK_S) { downPressed = true; }
        if (code == KeyEvent.VK_D) { rightPressed = true; }
        if (code == KeyEvent.VK_ENTER && !enterPressed) {
        enterJustPressed = true; // Only true on the frame the key is pressed
    }
    if (code == KeyEvent.VK_ENTER) { enterPressed = true; }
        if (code == KeyEvent.VK_SPACE) { spacePressed = true; }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode(); // get the key code (letter) of the released key

        if (code == KeyEvent.VK_W) { upPressed = false; }
        if (code == KeyEvent.VK_A) { leftPressed = false; }
        if (code == KeyEvent.VK_S) { downPressed = false; }
        if (code == KeyEvent.VK_D) { rightPressed = false; }
        if (code == KeyEvent.VK_ENTER) { enterPressed = false; }
        if (code == KeyEvent.VK_SPACE) { spacePressed = false; }
    }

    // accessors
    public boolean getUpPressed() { return upPressed; }
    public boolean getDownPressed() { return downPressed; }
    public boolean getLeftPressed() { return leftPressed; }
    public boolean getRightPressed() { return rightPressed; }
    public boolean getEnterPressed() { return enterPressed; }
    public boolean getSpacePressed() { return spacePressed; }
    // Accessor
public boolean getEnterJustPressed() { return enterJustPressed; }
public void resetEnterJustPressed() { enterJustPressed = false; }

    // temp
    public void setDownPressed(boolean b) {
        downPressed = b;
    }
    public void setLeftPressed(boolean b) {
        leftPressed = b;
    }
}
