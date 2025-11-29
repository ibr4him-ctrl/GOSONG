package view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import model.map.PizzaMap;
import model.map.tile.TileType;
import src.GUI.KeyHandler; 

public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS 
    private static final int ORIGINAL_TILE_SIZE = 16; // 16x16 tile
    private static final int SCALE = 3; // Scale factor
    private static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; // 48x48 pixels per tile
    private static final int MAX_SCREEN_COL = 16;
    private static final int MAX_SCREEN_ROW = 12; // 4:3 aspect ratio
    private static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL; // 768 pixels
    private static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW; // 576 pixels

    // Game objects
    private PizzaMap pizzaMap;
    private KeyHandler keyH = new KeyHandler(); 
    private Thread gameThread; 
    
    // Player properties
    private int player1X, player1Y;  // Player 1 position
    private int player2X, player2Y;  // Player 2 position
    private boolean isPlayer1Active = true;  // Tracks which player is active
    
    // Map properties
    private int mapOffsetX = 0;
    private int mapOffsetY = 0;
    private static final int PLAYER_SPEED = 4;


    public GamePanel(){
        // Initialize map
        pizzaMap = new PizzaMap();
        
        // Set player starting positions
        if (pizzaMap.getSpawnPoints().size() >= 2) {
            // Player 1 at first spawn point
            player1X = pizzaMap.getSpawnPoints().get(0).getX() * TILE_SIZE;
            player1Y = pizzaMap.getSpawnPoints().get(0).getY() * TILE_SIZE;
            
            // Player 2 at second spawn point
            player2X = pizzaMap.getSpawnPoints().get(1).getX() * TILE_SIZE;
            player2Y = pizzaMap.getSpawnPoints().get(1).getY() * TILE_SIZE;
        } else if (!pizzaMap.getSpawnPoints().isEmpty()) {
            // If only one spawn point, place players near each other
            player1X = pizzaMap.getSpawnPoints().get(0).getX() * TILE_SIZE;
            player1Y = pizzaMap.getSpawnPoints().get(0).getY() * TILE_SIZE;
            player2X = player1X + TILE_SIZE * 2; // Offset to the right
            player2Y = player1Y;
        } else {
            // Default positions if no spawn points
            player1X = 2 * TILE_SIZE;
            player1Y = 2 * TILE_SIZE;
            player2X = 5 * TILE_SIZE;
            player2Y = 2 * TILE_SIZE;
        }
        
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        // Disable focus traversal keys to allow TAB to be used for player switching
        this.setFocusTraversalKeysEnabled(false);
    }

    public void startGameThread(){
        gameThread = new Thread (this);
        gameThread.start();

    }

    @Override
    public void run(){ //this will be the gameLoop 
        double drawInterval = 1000000000 / 60; 
        double nextDrawTime = System.nanoTime() + drawInterval; 
        while (gameThread != null){ //while the gameThread exists 
            // System.out.println("The game loop is running"); 

            //1. UPDATE : update information such as character positions 
            update(); 
            //2. DRAW : draw the screen with the updated information 
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
        // Toggle active player when TAB is pressed and was not pressed in the previous frame
        if (keyH.tabPressed && !wasTabPressed) {
            isPlayer1Active = !isPlayer1Active;
            System.out.println("Switched to Player " + (isPlayer1Active ? "1" : "2"));
        }
        wasTabPressed = keyH.tabPressed;
        
        // Always update the currently active player
        if (isPlayer1Active) {
            updatePlayer(player1X, player1Y, true);
        } else {
            updatePlayer(player2X, player2Y, false);
        }
    }
    
    private void updatePlayer(int x, int y, boolean isPlayer1) {
        int newX = x;
        int newY = y;
        
        // Movement controls (WASD for both players)
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
        
        // Check if new position is valid and update the correct player
        if (isValidPosition(newX, newY)) {
            if (isPlayer1) {
                player1X = newX;
                player1Y = newY;
            } else {
                player2X = newX;
                player2Y = newY;
            }
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
        
        // Draw the map
        for (int y = 0; y < PizzaMap.HEIGHT; y++) {
            for (int x = 0; x < PizzaMap.WIDTH; x++) {
                int screenX = x * TILE_SIZE - mapOffsetX;
                int screenY = y * TILE_SIZE - mapOffsetY;
                
                // Only draw tiles that are visible on screen
                if (screenX + TILE_SIZE > 0 && screenX < getWidth() &&
                    screenY + TILE_SIZE > 0 && screenY < getHeight()) {
                    
                    // Get tile color based on type
                    switch (pizzaMap.getTileAt(x, y).getType()) {
                        case WALL:
                            g2.setColor(Color.DARK_GRAY);
                            break;
                        case WALKABLE:
                            g2.setColor(new Color(200, 200, 200)); // Floor
                            break;
                        case ASSEMBLY_STATION:
                            g2.setColor(new Color(139, 69, 19)); // Brown
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
                            g2.setColor(new Color(128, 0, 0)); // Dark Red
                            break;
                        default:
                            g2.setColor(Color.WHITE);
                    }
                    
                    g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                    
                    // Draw grid lines
                    g2.setColor(Color.BLACK);
                    g2.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        
        // Draw player 1 (Blue, with highlight if active)
        if (isPlayer1Active) {
            g2.setColor(new Color(100, 200, 255)); // Brighter blue for active player
        } else {
            g2.setColor(Color.BLUE);
        }
        g2.fillRect(player1X - mapOffsetX, player1Y - mapOffsetY, TILE_SIZE, TILE_SIZE);
        g2.setColor(Color.BLACK);
        g2.drawRect(player1X - mapOffsetX, player1Y - mapOffsetY, TILE_SIZE, TILE_SIZE);
        
        // Draw player 2 (Red, with highlight if active)
        if (!isPlayer1Active) {
            g2.setColor(new Color(255, 100, 100)); // Brighter red for active player
        } else {
            g2.setColor(Color.RED);
        }
        g2.fillRect(player2X - mapOffsetX, player2Y - mapOffsetY, TILE_SIZE, TILE_SIZE);
        g2.setColor(Color.BLACK);
        g2.drawRect(player2X - mapOffsetX, player2Y - mapOffsetY, TILE_SIZE, TILE_SIZE);
        
        // Draw which player is active
        g2.setColor(Color.WHITE);
        g2.drawString("Active: Player " + (isPlayer1Active ? "1 (WASD)" : "2 (WASD)"), 10, 20);
        g2.drawString("Press TAB to switch players", 10, 40);
        
        g2.dispose();
    }
}
