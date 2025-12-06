package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import model.chef.Chef;
import model.map.PizzaMap;
import src.GUI.KeyHandler;
import view.PlayerSprite.Direction;

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
    
    // Sprite
    private PlayerSprite chef1Sprite;
    private PlayerSprite chef2Sprite;
    
    // Map properties (kalau nanti mau scrolling)
    private int mapOffsetX = 0;
    private int mapOffsetY = 0;

    // pergerakan: 1 tile per langkah, pakai cooldown
    private static final long MOVE_COOLDOWN_NS = 150_000_000L; // 150 ms
    private long lastMoveTimeChef1 = 0L;
    private long lastMoveTimeChef2 = 0L;

    public GamePanel(){

        pizzaMap = new PizzaMap();
        
        if (pizzaMap.getSpawnPoints().size() >= 2) {
            int chef1X = pizzaMap.getSpawnPoints().get(0).getX();
            int chef1Y = pizzaMap.getSpawnPoints().get(0).getY();
            chef1 = new Chef(new model.map.Position(chef1X, chef1Y));
            chef1.setName("Chef 1");
            
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

        chef1Sprite = new PlayerSprite();
        chef2Sprite = new PlayerSprite();

        
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
    }

    public void startGameThread(){
        gameThread = new Thread (this);
        gameThread.start();
    }

    @Override
    public void run(){ 
        double drawInterval = 1000000000.0 / 60.0; 
        double nextDrawTime = System.nanoTime() + drawInterval; 
        while (gameThread != null){ 

            update(); 
            repaint(); 

            try {
                double remainingTime = nextDrawTime - System.nanoTime(); 
                remainingTime = remainingTime / 1_000_000.0; 

                if (remainingTime < 0) remainingTime = 0; 
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval; 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean wasTabPressed = false;
    
    public void update(){
        updateActivePlayer();
    }

    private void updateActivePlayer() {

        // handle switch chef
        if (keyH.tabPressed && !wasTabPressed) {
            isPlayer1Active = !isPlayer1Active;
            System.out.println("Switched to " + (isPlayer1Active ? chef1.getName() : chef2.getName()));
        }
        wasTabPressed = keyH.tabPressed;
        
        if (isPlayer1Active) {
            updateChef(chef1, true);
        } else {
            updateChef(chef2, false);
        }
    }

    /**
     * Gerak 1 tile per langkah, dengan cooldown supaya nggak ngebut.
     */
    private void updateChef(Chef chef, boolean isFirstChef) {
        long now = System.nanoTime();
        long lastMoveTime = isFirstChef ? lastMoveTimeChef1 : lastMoveTimeChef2;

        // batasi gerakan biar nggak tiap frame loncat
        if (now - lastMoveTime < MOVE_COOLDOWN_NS) {
            if (isFirstChef) chef1Sprite.updateAnimation(isAnyMoveKeyPressed());
            else             chef2Sprite.updateAnimation(isAnyMoveKeyPressed());
            return;
        }

        int tileX = chef.getPosition().getX();
        int tileY = chef.getPosition().getY();

        int newTileX = tileX;
        int newTileY = tileY;

        boolean moving = false; 

        // prioritas 1 arah dulu (biar nggak diagonal aneh2)
        if (keyH.wPressed) {
            newTileY--;
            moving = true;
            if (isFirstChef) chef1Sprite.setDirection(Direction.NORTH);
            else             chef2Sprite.setDirection(Direction.NORTH);
        } else if (keyH.sPressed) {
            newTileY++;
            moving = true;
            if (isFirstChef) chef1Sprite.setDirection(Direction.SOUTH);
            else             chef2Sprite.setDirection(Direction.SOUTH);
        } else if (keyH.aPressed) {
            newTileX--;
            moving = true;
            if (isFirstChef) chef1Sprite.setDirection(Direction.WEST);
            else             chef2Sprite.setDirection(Direction.WEST);
        } else if (keyH.dPressed) {
            newTileX++;
            moving = true;
            if (isFirstChef) chef1Sprite.setDirection(Direction.EAST);
            else             chef2Sprite.setDirection(Direction.EAST);
        }

        if (isFirstChef) {
            chef1Sprite.updateAnimation(moving);
        } else {
            chef2Sprite.updateAnimation(moving);
        }

        if (!moving) {
            // nggak ada tombol gerak → jangan pindah tile
            return;
        }

        if (isValidTile(newTileX, newTileY)) {
            chef.setPosition(newTileX, newTileY);
            // update lastMoveTime buat chef aktif
            if (isFirstChef) {
                lastMoveTimeChef1 = now;
            } else {
                lastMoveTimeChef2 = now;
            }
        }
    }
    private boolean isAnyMoveKeyPressed() {
        return keyH.wPressed || keyH.sPressed || keyH.aPressed || keyH.dPressed;
    }

    private boolean isValidTile(int tileX, int tileY) {
        return tileX >= 0 && tileX < PizzaMap.WIDTH &&
               tileY >= 0 && tileY < PizzaMap.HEIGHT &&
               pizzaMap.isWalkable(tileX, tileY);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // gambar map
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
        
        // gambar chef 1
        int chef1X = chef1.getPosition().getX() * TILE_SIZE - mapOffsetX;
        int chef1Y = chef1.getPosition().getY() * TILE_SIZE - mapOffsetY;
        
        chef1Sprite.draw(g2, chef1X, chef1Y, TILE_SIZE, isPlayer1Active);
        
        // gambar chef 2
        int chef2X = chef2.getPosition().getX() * TILE_SIZE - mapOffsetX;
        int chef2Y = chef2.getPosition().getY() * TILE_SIZE - mapOffsetY;
        chef2Sprite.draw(g2, chef2X, chef2Y, TILE_SIZE, !isPlayer1Active);
        
        g2.setColor(Color.WHITE);
        g2.drawString("Active: " + (isPlayer1Active ? chef1.getName() : chef2.getName()) + " (WASD)", 10, 20);
        g2.drawString("Press TAB to switch chefs", 10, 40);
        
        g2.dispose();
    }
}
