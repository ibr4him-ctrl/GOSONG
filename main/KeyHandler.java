import java.awt.event.KeyEvent; 
import java.awt.event.KeyListener; 


public class KeyHandler implements KeyListener{
    public boolean upPressed, downPressed, rightPressed, leftPressed; 
    @Override 
    public void keyTyped(KeyEvent e){

    }
    @Override
    public void keyPressed(KeyEvent e){ //Artinya hanya satu arah yang ke-exececute per frame
        int code = e.getKeyCode(); 
        

        if (code == KeyEvent.VK_W){ //if user press W key 
            upPressed = true; 
        }
        if (code == KeyEvent.VK_S){ //if user press S key 
            downPressed = true; 
        }
        if (code == KeyEvent.VK_A){ //if user press A key 
            leftPressed = true; 
        }
        if (code == KeyEvent.VK_D){ //if user press D key 
            rightPressed = true;  
        }
    }
    @Override
    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode(); 
        

        if (code == KeyEvent.VK_W){ //if user press W key 
            upPressed = false; 
        }
        if (code == KeyEvent.VK_S){ //if user press S key 
            downPressed = false;       
        }
        if (code == KeyEvent.VK_A){ //if user press A key 
            leftPressed = false;     
        }
        if (code == KeyEvent.VK_D){ //if user press D key 
           rightPressed = false;      
        }
    }

}