package view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import model.chef.Chef;
import model.map.PizzaMap;
import model.map.tile.TileType;
import src.GUI.KeyHandler; 

public class GamePanel extends JPanel implements Runnable {
    
    // setting layar 
    private static final int ORIGINAL_TILE_SIZE = 16;
    private static final int SCALE = 3; 
    private static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    private static final int MAX_SCREEN_COL = 16;
    private static final int MAX_SCREEN_ROW = 12;
    private static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    private static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

    // object game
    private PizzaMap pizzaMap;
    private KeyHandler keyH = new KeyHandler(); 
    private Thread gameThread; 
    
    // Chef
    private Chef chef1;  // Chef 1
    private Chef chef2;  // Chef 2
    private boolean isPlayer1Active = true;  
    
    // Map properties
    private int mapOffsetX = 0;
    private int mapOffsetY = 0;
    private static final int PLAYER_SPEED = 4;


    public GamePanel(){

        pizzaMap = new PizzaMap();
        
        if (pizzaMap.getSpawnPoints().size() >= 2) {
            // Chef 1 di spawn point pertama
            int chef1X = pizzaMap.getSpawnPoints().get(0).getX();
            int chef1Y = pizzaMap.getSpawnPoints().get(0).getY();
            chef1 = new Chef(new model.map.Position(chef1X, chef1Y));
            chef1.setName("Chef 1");
            
            // Chef 2 di spawn point kedua
            int chef2X = pizzaMap.getSpawnPoints().get(1).getX();
            int chef2Y = pizzaMap.getSpawnPoints().get(1).getY();
            chef2 = new Chef(new model.map.Position(chef2X, chef2Y));
            chef2.setName("Chef 2");
        } else if (!pizzaMap.getSpawnPoints().isEmpty()) {

            int chef1X = pizzaMap.getSpawnPoints().get(0).getX();
            int chef1Y = pizzaMap.getSpawnPoints().get(0).getY();
            chef1 = new Chef(new model.map.Position(chef1X, chef1Y));
            chef1.setName("Chef 1");
            
            chef2 = new Chef(new model.map.Position(chef1X + 2, chef1Y));
            chef2.setName("Chef 2");
        } else {
            chef1 = new Chef(new model.map.Position(2, 2));
            chef1.setName("Chef 1");
            
            chef2 = new Chef(new model.map.Position(5, 2));
            chef2.setName("Chef 2");
        }
        
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        this.setFocusTraversalKeysEnabled(false);
    }

    public void startGameThread(){
        gameThread = new Thread (this);
        gameThread.start();

    }

    @Override
    public void run(){ 
        double drawInterval = 1000000000 / 60; 
        double nextDrawTime = System.nanoTime() + drawInterval; 
        while (gameThread != null){ 

            update(); 

            repaint(); 

            try {
                double remainingTime = nextDrawTime - System.nanoTime(); 
                remainingTime = remainingTime / 1000000; 

                if (remainingTime < 0) remainingTime = 0; 
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval; 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean wasTabPressed = false;
    
    private void updateActivePlayer() {

        if (keyH.tabPressed && !wasTabPressed) {
            isPlayer1Active = !isPlayer1Active;
            System.out.println("Switched to " + (isPlayer1Active ? chef1.getName() : chef2.getName()));
        }
        wasTabPressed = keyH.tabPressed;
        
        if (isPlayer1Active) {
            updateChef(chef1);
        } else {
            updateChef(chef2);
        }
    }
    
    private void updateChef(Chef chef) {
        int currentX = chef.getPosition().getX() * TILE_SIZE;
        int currentY = chef.getPosition().getY() * TILE_SIZE;
        int newX = currentX;
        int newY = currentY;
        
        if (keyH.wPressed) {
            newY -= PLAYER_SPEED;
        }
        if (keyH.sPressed) {
            newY += PLAYER_SPEED;
        }
        if (keyH.aPressed) {
            newX -= PLAYER_SPEED;
        }
        if (keyH.dPressed) {
            newX += PLAYER_SPEED;
        }
        
        int newTileX = newX / TILE_SIZE;
        int newTileY = newY / TILE_SIZE;
        
        if (isValidPosition(newX, newY) && 
            (newTileX != chef.getPosition().getX() || newTileY != chef.getPosition().getY())) {
            chef.setPosition(newTileX, newTileY);
        }
    }
    
    private boolean isValidPosition(int x, int y) {
        int tileX = x / TILE_SIZE;
        int tileY = y / TILE_SIZE;
        
        return tileX >= 0 && tileX < PizzaMap.WIDTH &&
               tileY >= 0 && tileY < PizzaMap.HEIGHT &&
               pizzaMap.isWalkable(tileX, tileY);
    }
    
    public void update(){
        updateActivePlayer();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        for (int y = 0; y < PizzaMap.HEIGHT; y++) {
            for (int x = 0; x < PizzaMap.WIDTH; x++) {
                int screenX = x * TILE_SIZE - mapOffsetX;
                int screenY = y * TILE_SIZE - mapOffsetY;
                
                if (screenX + TILE_SIZE > 0 && screenX < getWidth() &&
                    screenY + TILE_SIZE > 0 && screenY < getHeight()) {
                    
                    switch (pizzaMap.getTileAt(x, y).getType()) {
                        case WALL:
                            g2.setColor(Color.DARK_GRAY);
                            break;
                        case WALKABLE:
                            g2.setColor(new Color(200, 200, 200)); 
                            break;
                        case ASSEMBLY_STATION:
                            g2.setColor(new Color(139, 69, 19)); 
                            break;
                        case COOKING_STATION:
                            g2.setColor(Color.RED);
                            break;
                        case SPAWN_CHEF:
                            g2.setColor(Color.GREEN);
                            break;
                        case CUTTING_STATION:
                            g2.setColor(Color.ORANGE);
                            break;
                        case PLATE_STORAGE:
                            g2.setColor(Color.CYAN);
                            break;
                        case INGREDIENT_STORAGE:
                            g2.setColor(Color.YELLOW);
                            break;
                        case SERVING_COUNTER:
                            g2.setColor(Color.MAGENTA);
                            break;
                        case WASHING_STATION:
                            g2.setColor(Color.BLUE);
                            break;
                        case TRASH:
                            g2.setColor(new Color(128, 0, 0)); 
                            break;
                        default:
                            g2.setColor(Color.WHITE);
                    }
                    
                    g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    
                    g2.setColor(Color.BLACK);
                    g2.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        
        int chef1X = chef1.getPosition().getX() * TILE_SIZE - mapOffsetX;
        int chef1Y = chef1.getPosition().getY() * TILE_SIZE - mapOffsetY;
        
        if (isPlayer1Active) {
            g2.setColor(new Color(100, 200, 255));
        } else {
            g2.setColor(Color.BLUE);
        }
        g2.fillRect(chef1X, chef1Y, TILE_SIZE, TILE_SIZE);
        g2.setColor(Color.BLACK);
        g2.drawRect(chef1X, chef1Y, TILE_SIZE, TILE_SIZE);
        
        int chef2X = chef2.getPosition().getX() * TILE_SIZE - mapOffsetX;
        int chef2Y = chef2.getPosition().getY() * TILE_SIZE - mapOffsetY;
        
        if (!isPlayer1Active) {
            g2.setColor(new Color(255, 100, 100));
        } else {
            g2.setColor(Color.RED);
        }
        g2.fillRect(chef2X, chef2Y, TILE_SIZE, TILE_SIZE);
        g2.setColor(Color.BLACK);
        g2.drawRect(chef2X, chef2Y, TILE_SIZE, TILE_SIZE);
        
        g2.setColor(Color.WHITE);
        g2.drawString("Active: " + (isPlayer1Active ? chef1.getName() : chef2.getName()) + " (WASD)", 10, 20);
        g2.drawString("Press TAB to switch chefs", 10, 40);
        
        g2.dispose();
    }
}
