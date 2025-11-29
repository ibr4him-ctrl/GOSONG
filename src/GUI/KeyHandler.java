package src.GUI;
import java.awt.event.KeyEvent; 
import java.awt.event.KeyListener; 

public class KeyHandler implements KeyListener {
    // Movement controls (WASD for both players)
    public boolean wPressed, sPressed, aPressed, dPressed;
    // Toggle key to switch between players
    public boolean tabPressed;
    @Override 
    public void keyTyped(KeyEvent e){

    }
    @Override
    public void keyPressed(KeyEvent e) { 
        int code = e.getKeyCode();
        
        // Movement controls (WASD)
        if (code == KeyEvent.VK_W) {  
            wPressed = true; 
        } else if (code == KeyEvent.VK_S) {  
            sPressed = true; 
        } else if (code == KeyEvent.VK_A) {  
            aPressed = true; 
        } else if (code == KeyEvent.VK_D) {  
            dPressed = true;  
        } 
        // Toggle active player with TAB
        else if (code == KeyEvent.VK_TAB) {
            tabPressed = true;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        // Movement controls (WASD)
        if (code == KeyEvent.VK_W) {  
            wPressed = false; 
        } else if (code == KeyEvent.VK_S) {  
            sPressed = false; 
        } else if (code == KeyEvent.VK_A) {  
            aPressed = false; 
        } else if (code == KeyEvent.VK_D) {  
            dPressed = false;  
        }
        // Toggle key release
        else if (code == KeyEvent.VK_TAB) {
            tabPressed = false;
        }
    }

}
