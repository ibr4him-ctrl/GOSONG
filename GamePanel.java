

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel; 

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS 

    final int originalTileSize = 16; // 16x16 tile (opsional, ini bisa diganti)
    final int scale = 3; //perkalian sizenya 

    final int tileSize = originalTileSize * scale; // 48 x 48 tile (actual tile size)
    final int maxScreenCol = 16; 
    final int maxScreenRow = 12; //ratio nya 4 by 3; 
    final int screenWidth = tileSize * maxScreenCol; //768 pixels 
    final int screenHeight = tileSize * maxScreenRow; //576 pixels 

    Thread gameThread; 
    
    int FPS = 60; 


    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

    }

    public void startGameThread(){
        gameThread = new Thread (this);
        gameThread.start();

    }

    @Override
    public void run(){ //this will be the gameLoop 
        while (gameThread != null){ //while the gameThread exists 
            // System.out.println("The game loop is running"); 

            //1. UPDATE : update information such as character positions 
            update(); 
            //2. DRAW : draw the screen with the updated information 
            repaint(); 
        }
    }

    public void update(){

    }

    public void paintComponent(Graphics g){
        
        super.paintComponent(g); 

        Graphics2D g2 = (Graphics2D)g; 
        g2.setColor(Color.white);

        g2.fillRect(100, 100, tileSize, tileSize);

        g2.dispose();

    }
}