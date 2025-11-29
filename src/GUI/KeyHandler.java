package src.GUI;
import java.awt.event.KeyEvent; 
import java.awt.event.KeyListener; 


public class KeyHandler implements KeyListener {
    // Player 1 (WASD) controls
    public boolean wPressed, sPressed, aPressed, dPressed;
    
    // Player 2 (Arrow keys) controls
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    @Override 
    public void keyTyped(KeyEvent e){

    }
    @Override
    public void keyPressed(KeyEvent e) { 
        int code = e.getKeyCode();
        
        // Player 1 (WASD) controls
        if (code == KeyEvent.VK_W) {  
            wPressed = true; 
        } else if (code == KeyEvent.VK_S) {  
            sPressed = true; 
        } else if (code == KeyEvent.VK_A) {  
            aPressed = true; 
        } else if (code == KeyEvent.VK_D) {  
            dPressed = true;  
        }
        // Player 2 (Arrow keys) controls
        else if (code == KeyEvent.VK_UP) {  
            upPressed = true; 
        } else if (code == KeyEvent.VK_DOWN) {  
            downPressed = true; 
        } else if (code == KeyEvent.VK_LEFT) {  
            leftPressed = true; 
        } else if (code == KeyEvent.VK_RIGHT) {  
            rightPressed = true;  
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        // Player 1 (WASD) controls
        if (code == KeyEvent.VK_W) {  
            wPressed = false; 
        } else if (code == KeyEvent.VK_S) {  
            sPressed = false; 
        } else if (code == KeyEvent.VK_A) {  
            aPressed = false; 
        } else if (code == KeyEvent.VK_D) {  
            dPressed = false;  
        }
        // Player 2 (Arrow keys) controls
        else if (code == KeyEvent.VK_UP) {  
            upPressed = false; 
        } else if (code == KeyEvent.VK_DOWN) {  
            downPressed = false; 
        } else if (code == KeyEvent.VK_LEFT) {  
            leftPressed = false; 
        } else if (code == KeyEvent.VK_RIGHT) {  
            rightPressed = false;  
        }
    }

}
