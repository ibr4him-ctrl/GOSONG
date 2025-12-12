package view;

import actions.useStation.AssemblyAction;
import actions.useStation.CookingAction;
import actions.useStation.WashingAction;
import controller.PickUpDrop;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import model.chef.Chef;
import model.item.Dish;
import model.item.Item;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Cheese;
import model.item.ingredient.pizza.Chicken;
import model.item.ingredient.pizza.Dough;
import model.item.ingredient.pizza.Sausage;
import model.item.ingredient.pizza.Tomato;
import model.item.utensils.Plate;
import model.manager.OrderManager;
import model.map.PizzaMap;
import model.map.Position;
import model.map.tile.Tile;
import model.map.tile.TileType;
import model.station.AssemblyStation;
import model.station.CookingStation;
import model.station.CuttingStation;
import model.station.IngredientStorage;
import model.station.PlateStorage;
import model.station.ServingCounter;
import model.station.Station;
import model.station.TrashStation;
import model.station.WashingStation;
import src.GUI.KeyHandler;
import view.PlayerSprite.Direction;
import controller.DashController;
import controller.ThrowController;


public class GamePanel extends JPanel implements Runnable {

    private static final int ORIGINAL_TILE_SIZE = 16;
    private static final int SCALE = 3;
    private static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    private static final int MAX_SCREEN_COL = 16;
    private static final int MAX_SCREEN_ROW = 12;
    private static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    private static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;
    private AssemblyRenderer assemblyRenderer = new AssemblyRenderer();
    private TileRenderer tileRenderer = new TileRenderer();
    
    private BufferedImage ijo0Image;
    private BufferedImage ijo1Image;
    private BufferedImage ijo2Image;

    // Background kertas order untuk 3 blok order
    private BufferedImage orderPaper1;
    private BufferedImage orderPaper2;
    private BufferedImage orderPaper3;

    // Icon dan panel UI tambahan
    private BufferedImage settingsIconImage;
    private BufferedImage chefInventory1Image;
    private BufferedImage chefInventory2Image;

    private Map<String, Station> stationMap = new HashMap<>();

    private Map<String, Item> groundItems = new HashMap<>();

    private String groundKey(int x, int y) {
        return x + "," + y;
    }

    private PizzaMap pizzaMap;
    private KeyHandler keyH = new KeyHandler();
    private Thread gameThread;

    private Chef chef1;  
    private Chef chef2;  
    private boolean isPlayer1Active = true;

    private PlayerSprite chef1Sprite;
    private PlayerSprite chef2Sprite;

    private int mapOffsetX = 0;
    private int mapOffsetY = 0;

    private static final long MOVE_COOLDOWN_NS = 150_000_000L; // 150 ms
    private long lastMoveTimeChef1 = 0L;
    private long lastMoveTimeChef2 = 0L;

    private boolean wasTabPressed = false;
    private boolean wasCPressed = false;
    private boolean wasVPressed = false;

    private long lastUpdateNs = System.nanoTime();
    
    private int settingButtonX, settingButtonY, settingButtonWidth, settingButtonHeight;

    private DashController dashController = new DashController();
    private ThrowController throwController = new ThrowController();
    private boolean wasEPressed = false;
    private int selectedThrowDistance = 3;

    public GamePanel() {

        pizzaMap = new PizzaMap();
        initStationsFromMap();  
        initPlateStorages();

        if (pizzaMap.getSpawnPoints().size() >= 2) {
            Position p1 = pizzaMap.getSpawnPoints().get(0);
            Position p2 = pizzaMap.getSpawnPoints().get(1);

            chef1 = new Chef(new Position(p1));
            chef1.setName("Chef 1");

            chef2 = new Chef(new Position(p2));
            chef2.setName("Chef 2");
        } else if (!pizzaMap.getSpawnPoints().isEmpty()) {
            Position p1 = pizzaMap.getSpawnPoints().get(0);

            chef1 = new Chef(new Position(p1));
            chef1.setName("Chef 1");

            chef2 = new Chef(new Position(p1.getX() + 2, p1.getY()));
            chef2.setName("Chef 2");
        } else {
            chef1 = new Chef(new Position(2, 2));
            chef1.setName("Chef 1");

            chef2 = new Chef(new Position(5, 2));
            chef2.setName("Chef 2");
        }

        // Chef 1: folder Chef1, warna blue
        chef1Sprite = new PlayerSprite("Chef1", "blue");
        // Chef 2: folder Chef2, warna red
        chef2Sprite = new PlayerSprite("Chef2", "red");

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();
                
                if (mx >= settingButtonX && mx <= settingButtonX + settingButtonWidth &&
                    my >= settingButtonY && my <= settingButtonY + settingButtonHeight) {
                    SettingsEditor settings = new SettingsEditor((javax.swing.JFrame) getTopLevelAncestor());
                    settings.setVisible(true);
                }
            }
        });

        // INI ORDER SYSTEM 
        OrderManager.getInstance().init();

        // RESET SCORE DI AWAL GAME
        model.manager.ScoreManager.getInstance().reset();
        
        loadOrderIndicatorImages();
        loadOrderPaperImages();
        loadUiOverlayImages();
        
        SettingsEditor.setMusicPlayer(main.Main.getMusicPlayer());
    }
    
    
    private void loadOrderIndicatorImages() {
        try {
            ijo0Image = ImageIO.read(getClass().getResource("/resources/game/ijo0.png"));
            ijo1Image = ImageIO.read(getClass().getResource("/resources/game/ijo1.png"));
            ijo2Image = ImageIO.read(getClass().getResource("/resources/game/ijo2.png"));
            System.out.println("Order indicator images loaded successfully");
        } catch (IOException e) {
            System.err.println("Failed to load order indicator images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadOrderPaperImages() {
        try {
            orderPaper1 = ImageIO.read(getClass().getResource("/resources/game/KertasOrderan.png"));
            orderPaper2 = ImageIO.read(getClass().getResource("/resources/game/KertasOrderan2.png"));
            orderPaper3 = ImageIO.read(getClass().getResource("/resources/game/KertasOrderan3.png"));
            System.out.println("Order paper images loaded successfully");
        } catch (IOException e) {
            System.err.println("Failed to load order paper images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUiOverlayImages() {
        try {
            URL url = getClass().getResource("/resources/game/SettingsIcon.png");
            if (url != null) {
                settingsIconImage = ImageIO.read(url);
            }
        } catch (IOException e) {
            System.err.println("Failed to load Pengaturan.png: " + e.getMessage());
        }

        try {
            URL inv1 = getClass().getResource("/resources/item/inventory/1.png");
            URL inv2 = getClass().getResource("/resources/item/inventory/2.png");
            if (inv1 != null) {
                chefInventory1Image = ImageIO.read(inv1);
            }
            if (inv2 != null) {
                chefInventory2Image = ImageIO.read(inv2);
            }
        } catch (IOException e) {
            System.err.println("Failed to load chef inventory images: " + e.getMessage());
        }
    }
    
    private BufferedImage getOrderIndicatorImage(int timeRemaining) {
        // Untuk order dengan maksimal 80 detik:
        // 80-53 detik : ijo0.png
        // 52-27 detik : ijo1.png
        // 26-0 detik  : ijo2.png
        if (timeRemaining > 52) {
            return ijo0Image;
        } else if (timeRemaining > 26) {
            return ijo1Image;
        } else {
            return ijo2Image;
        }
    }

    // ==========================================
    // INIT STATION DARI PIZZAMAP
    // ==========================================
    private void initStationsFromMap() {
        for (int y = 0; y < PizzaMap.HEIGHT; y++) {
            for (int x = 0; x < PizzaMap.WIDTH; x++) {
                TileType type = pizzaMap.getTileAt(x, y).getType();
                Station st = null;

                switch (type) {
                    case ASSEMBLY_STATION -> st = new AssemblyStation(x, y);
                    case COOKING_STATION  -> st = new CookingStation(x, y);
                    case WASHING_STATION  -> st = new WashingStation(x, y);
                    case CUTTING_STATION  -> st = new CuttingStation(x, y);
                    case PLATE_STORAGE    -> st = new PlateStorage(x, y);
                    case INGREDIENT_STORAGE -> {
                        Class<? extends Ingredient> ingType = null;
                        String ingName = null;

                        // y & x di sini 0-based, sesuai LAYOUT di PizzaMap
                        if (x == 4 && y == 4) {
                            ingType = Dough.class;
                            ingName = "Dough";
                        } else if (x == 6 && y == 4) {
                            ingType = Tomato.class;
                            ingName = "Tomato";
                        } else if (x == 8 && y == 4) {
                            ingType = Cheese.class;
                            ingName = "Cheese";
                        } else if (x == 10 && y == 4) {
                            ingType = Chicken.class;
                            ingName = "Chicken";
                        } else if (x == 6 && y == 9) {
                            ingType = Sausage.class;
                            ingName = "Sausage";
                        }
                        if (ingType != null) {
                            st = new IngredientStorage(x, y, ingType, ingName);
                        } else {
                            // fallback biar nggak NPE kalau ada 'I' lain yang belum di-mapping
                            st = new IngredientStorage(x, y, Dough.class, "Dough");
                            System.out.println("IngredientStorage di (" + x + "," + y +
                                            ") belum di-set spesifik, pakai Dough default.");
                        }
                    }
                    case SERVING_COUNTER  -> st = new ServingCounter(x, y);
                    case TRASH            -> st = new TrashStation(x, y);
                    // WALL, WALKABLE, SPAWN_CHEF tidak perlu objek Station khusus
                    default -> { /* kosong */ }
                }

                if (st != null) {
                    stationMap.put(stationKey(x, y), st);
                }
            }
        }
        System.out.println("Stations initialized: " + stationMap.size());
    }

    private void initPlateStorages() {
        for (Station st : stationMap.values()) {
            if (st instanceof PlateStorage ps) {
                // misal: awalnya punya 5 piring bersih
                for (int i = 0; i < 5; i++) {
                    ps.pushInitialCleanPlate(new Plate());
                }
                System.out.println("Init PlateStorage di (" + ps.getPosX() + "," + ps.getPosY() +
                                ") dengan 5 clean plates");
            }
        }
    }

    private String stationKey(int x, int y) {
        return x + "," + y;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public synchronized void stopGameThread() {
        // Menghentikan loop game dengan membuat kondisi while(gameThread != null) menjadi false
        gameThread = null;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / 60.0;
        double nextDrawTime = System.nanoTime() + drawInterval;
        while (gameThread != null) {

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

    private void updateCuttingStations(double deltaSeconds) {
        for (Station st : stationMap.values()) {
            if (st instanceof CuttingStation cs) {
                cs.update(deltaSeconds);
            }
        }
    }

    private void updateCookingStations(double deltaSeconds) {
        for (Station st : stationMap.values()) {
            if (st instanceof CookingStation cs) {
                cs.update(deltaSeconds);
            }
        }
    }
    private void updateWashingStations(double deltaSeconds) {
        for (Station st : stationMap.values()) {
            if (st instanceof WashingStation ws) {
                ws.update(deltaSeconds);
            }
        }
    }
    // ==========================================
    // UPDATE LOOP
    // ==========================================
    public void update() {
        long now = System.nanoTime();
        double deltaSeconds = (now - lastUpdateNs) / 1_000_000_000.0;
        if (deltaSeconds < 0) deltaSeconds = 0;
        if (deltaSeconds > 0.1) deltaSeconds = 0.1; // clamp biar gak loncat kalo lag
        lastUpdateNs = now;

        updateActivePlayer();
        handleActions();
        updateCuttingStations(deltaSeconds);
        updateCookingStations(deltaSeconds);
        updateWashingStations(deltaSeconds); 

        OrderManager.getInstance().update(deltaSeconds);

        
    }

    private Chef getActiveChef() {
        return isPlayer1Active ? chef1 : chef2;
    }

    private PlayerSprite getActiveSprite() {
        return isPlayer1Active ? chef1Sprite : chef2Sprite;
    }

    private Station getStationInFrontOf(Chef chef, PlayerSprite sprite) {
        Position front = getTileInFrontPos(chef, sprite);
        if (front == null) return null;
        return stationMap.get(stationKey(front.getX(), front.getY()));
    }

    private Position getTileInFrontPos(Chef chef, PlayerSprite sprite) {
    int x = chef.getPosition().getX();
    int y = chef.getPosition().getY();

    int dx = 0, dy = 0;

    Direction dir = sprite.getDirection();
    switch (dir) {
        case NORTH      -> { dx = 0;  dy = -1; }
        case SOUTH      -> { dx = 0;  dy = 1;  }
        case EAST       -> { dx = 1;  dy = 0;  }
        case WEST       -> { dx = -1; dy = 0;  }
        case NORTHEAST  -> { dx = 1;  dy = -1; }
        case NORTHWEST  -> { dx = -1; dy = -1; }
        case SOUTHEAST  -> { dx = 1;  dy = 1;  }
        case SOUTHWEST  -> { dx = -1; dy = 1;  }
    }

    int tx = x + dx;
    int ty = y + dy;

    if (tx < 0 || tx >= PizzaMap.WIDTH || ty < 0 || ty >= PizzaMap.HEIGHT) {
        return null;
        }
    return new Position(tx, ty);
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

        int tileX = chef.getPosition().getX();
        int tileY = chef.getPosition().getY();

        int newTileX = tileX;
        int newTileY = tileY;

        boolean moving = false;

        if (keyH.shiftPressed) {
        char dashDirection = ' ';
        if (keyH.wPressed) dashDirection = 'W';
        else if (keyH.sPressed) dashDirection = 'S';
        else if (keyH.aPressed) dashDirection = 'A';
        else if (keyH.dPressed) dashDirection = 'D';
        
        if (dashDirection != ' ') {
            Tile[][] tileMap = pizzaMap.getTiles();
            
            DashController.DashResult dashResult = dashController.execute(
                chef, dashDirection, tileMap, isFirstChef, keyH.shiftPressed
            );
            
            if (dashResult.success) {
                PlayerSprite sprite = isFirstChef ? chef1Sprite : chef2Sprite;
                switch(dashDirection) {
                    case 'W': sprite.setDirection(Direction.NORTH); break;
                    case 'S': sprite.setDirection(Direction.SOUTH); break;
                    case 'A': sprite.setDirection(Direction.WEST); break;
                    case 'D': sprite.setDirection(Direction.EAST); break;
                }
                if (isFirstChef) lastMoveTimeChef1 = now;
                else lastMoveTimeChef2 = now;
                sprite.updateAnimation(true);
            } 
            return;
        }
    }


        // batasi gerakan biar nggak tiap frame loncat
        if (now - lastMoveTime < MOVE_COOLDOWN_NS) {
            if (isFirstChef) chef1Sprite.updateAnimation(isAnyMoveKeyPressed());
            else             chef2Sprite.updateAnimation(isAnyMoveKeyPressed());
            return;
        }

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
            if (isFirstChef) {
                lastMoveTimeChef1 = now;
            } else {
                lastMoveTimeChef2 = now;
            }
        }
    }

    private boolean isAnyMoveKeyPressed() {
        return keyH.wPressed || keyH.sPressed || keyH.aPressed || keyH.dPressed || keyH.shiftPressed;
    }

    private boolean isValidTile(int tileX, int tileY) {
        return tileX >= 0 && tileX < PizzaMap.WIDTH &&
               tileY >= 0 && tileY < PizzaMap.HEIGHT &&
               pizzaMap.isWalkable(tileX, tileY);
    }
    // Icon kecil di atas chef yang menunjukkan item yang sedang dipegang

    private void drawHeldItemIcon(Graphics2D g2, Chef chef, int screenX, int screenY) {
        var item = chef.getHeldItem();
        if (item == null) return; // gak pegang apa-apa

        int iconSize = TILE_SIZE / 3;  // ukuran bubble
        int x = screenX + TILE_SIZE - iconSize;  // pojok kanan atas tile
        int y = screenY - iconSize / 2;          // agak naik dikit

        // warna default
        Color fill = new Color(240, 240, 240);
        String label = "?";

        // Ingredient khusus pizza → warna beda & huruf awal
        if (item instanceof model.item.ingredient.pizza.Dough) {
            fill = new Color(210, 180, 140); // coklat dough
            label = "D";
        } else if (item instanceof model.item.ingredient.pizza.Tomato) {
            fill = Color.RED;
            label = "T";
        } else if (item instanceof model.item.ingredient.pizza.Cheese) {
            fill = Color.YELLOW;
            label = "C";
        } else if (item instanceof model.item.ingredient.pizza.Chicken) {
            fill = new Color(255, 220, 200);
            label = "K"; // ayam (chicKen) biar beda
        } else if (item instanceof model.item.ingredient.pizza.Sausage) {
            fill = new Color(180, 90, 60);
            label = "S";
        } 
        else if (item instanceof Plate plate) {
            fill = Color.WHITE;
            int count = plate.getContents().size();
            label = (count == 0) ? "P" : "P" + count;
        } 
        else {
            String simple = item.getClass().getSimpleName();
            if (!simple.isEmpty()) {
                label = simple.substring(0, 1).toUpperCase();
            }
        }

        // gambar bubble
        g2.setColor(fill);
        g2.fillOval(x, y, iconSize, iconSize);

        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, iconSize, iconSize);

        // tulis huruf di tengah
        g2.setFont(g2.getFont().deriveFont(10f));
        int textX = x + iconSize / 2 - 3;
        int textY = y + iconSize / 2 + 4;
        g2.drawString(label, textX, textY);
    }

    // HELPER: gambar item yang dipegang chef di panel inventory bawah
    private void drawInventoryHeldItemIcon(Graphics2D g2, Chef chef, int centerX, int baseY) {
        Item item = chef.getHeldItem();
        if (item == null) return;

        int iconSize = TILE_SIZE;
        int drawX = centerX - iconSize / 2;
        int drawY = baseY - iconSize;

        Color fill = new Color(255, 220, 200);
        String label = "?";

        if (item instanceof Plate plate) {
            fill = Color.WHITE;
            int count = plate.getContents().size();
            label = (count == 0) ? "Plate" : "P" + count;
        } else if (item instanceof Ingredient) {
            label = item.getName();
        } else if (item instanceof Dish) {
            label = "P";
        } else {
            label = item.getName();
        }

        g2.setColor(fill);
        g2.fillRoundRect(drawX, drawY, iconSize, iconSize, 8, 8);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(drawX, drawY, iconSize, iconSize, 8, 8);

        g2.setFont(g2.getFont().deriveFont(10f));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(label);
        int textX = drawX + (iconSize - textWidth) / 2;
        int textY = drawY + (iconSize + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(label, textX, textY);
    }

    private void handleActions() {
        Chef activeChef = getActiveChef();
        PlayerSprite activeSprite = getActiveSprite();
        Station stationInFront = getStationInFrontOf(activeChef, activeSprite);

        // === PICK UP / DROP: tombol C ===
        if (keyH.ePressed && !wasEPressed) {
        System.out.println(" ");
        
        // Tentukan arah throw
        Direction dir = activeSprite.getDirection();
        char throwDirection = 'S'; // default
        switch(dir) {
            case NORTH:     throwDirection = 'W'; break;
            case SOUTH:     throwDirection = 'S'; break;
            case WEST:      throwDirection = 'A'; break;
            case EAST:      throwDirection = 'D'; break;
            case NORTHEAST: throwDirection = 'W'; break;
            case NORTHWEST: throwDirection = 'W'; break;
            case SOUTHEAST: throwDirection = 'S'; break;
            case SOUTHWEST: throwDirection = 'S'; break;
        }
        
        // Get chef lain (untuk menangkap)
        Chef otherChef = isPlayer1Active ? chef2 : chef1;
        
        // Convert map
        Tile[][] tileMap = pizzaMap.getTiles();
        
        // Execute throw
        ThrowController.ThrowResult throwResult = throwController.execute(
            activeChef,
            throwDirection,
            selectedThrowDistance, // 2-4 tiles (default 3)
            tileMap,
            otherChef
        );
        
        if (throwResult.success) {
            if (throwResult.caught) {
                System.out.println("[Throw] SUCCESS - Caught by " + otherChef.getName());
            } else {
                // Item jatuh ke lantai
                int landX = throwResult.landingPosition.getX();
                int landY = throwResult.landingPosition.getY();
                String key = groundKey(landX, landY);
                
                // Cek apakah lantai sudah ada item
                if (groundItems.containsKey(key)) {
                    System.out.println("[Throw] Lantai sudah penuh! Kembalikan item.");
                    activeChef.setHeldItem(throwResult.ingredient);
                } else {
                    groundItems.put(key, throwResult.ingredient);
                    System.out.println("[Throw] Item jatuh di " + landX + "," + landY);
                }
            }
        } else {
            System.out.println("[Throw] FAILED - " + throwResult.message);
        }
    }
    wasEPressed = keyH.ePressed;

        if (keyH.cPressed && !wasCPressed) {
            System.out.println("[GUI] C pressed by " + activeChef.getName());

            // Hitung posisi tile di depan (buat ground logic)
            Position frontPos = getTileInFrontPos(activeChef, activeSprite);

            if (stationInFront != null) {
                // === KHUSUS COOKING STATION (OVEN) → pakai CookingAction lewat tombol C ===
                if (stationInFront instanceof CookingStation) {
                    System.out.println("[GUI] C pressed on CookingStation → delegasi ke CookingAction.");
                    boolean ok = new CookingAction().execute(activeChef, stationInFront);
                    if (!ok) {
                        System.out.println("CookingAction (via C) gagal.");
                    }
                } else if (stationInFront instanceof AssemblyStation) {
                    boolean ok = new AssemblyAction().execute(activeChef, stationInFront);
                } else if (stationInFront instanceof PlateStorage) {
                    System.out.println("[GUI] Untuk ambil / balikin plate, gunakan tombol V (Use Station).");
                } else if (stationInFront instanceof ServingCounter sc) {
                    boolean ok = sc.interact(activeChef);
                    if (!ok) {
                        System.out.println("[ServingCounter] Gagal serve (cek: pegang dish sesuai order & plate bersih).");
                    }
                } else if (stationInFront instanceof TrashStation ts) {
                    boolean ok = ts.interact(activeChef);
                    if (!ok) {
                        System.out.println("[TrashStation] Gagal membuang item. (cek: jarak / jenis item / aturan trash)");
                    }
                } else {
                    // Station lain tetap pakai PickUpDrop
                    boolean ok = new PickUpDrop().execute(activeChef, stationInFront);
                    if (!ok) {
                        System.out.println("PickUpDrop gagal atau tidak ada station cocok.");
                    }
                }
            } else if (frontPos != null) {
                // TIDAK ada station → coba drop / pick di LANTAI
                int tx = frontPos.getX();
                int ty = frontPos.getY();

                // Pastikan tile-nya WALKABLE (atau boleh juga SPAWN_CHEF kalau mau)
                TileType tt = pizzaMap.getTileAt(tx, ty).getType();
                if (tt != TileType.WALKABLE) {
                    System.out.println("[Ground] Tile depan bukan WALKABLE, tidak bisa drop di lantai.");
                } else {
                    String key = groundKey(tx, ty);
                    Item hand = activeChef.getHeldItem();
                    Item ground = groundItems.get(key);

                    // Tangan kosong + ada item di lantai → ambil
                    if (hand == null && ground != null) {
                        activeChef.setHeldItem(ground);
                        groundItems.remove(key);
                        System.out.println("[Ground] Chef mengambil " + ground.getName() +
                                           " dari lantai (" + tx + "," + ty + ")");
                    }
                    // Tangan pegang item + lantai kosong → taruh
                    else if (hand != null && ground == null) {
                        groundItems.put(key, hand);
                        activeChef.setHeldItem(null);
                        System.out.println("[Ground] Chef meletakkan " + hand.getName() +
                                           " di lantai (" + tx + "," + ty + ")");
                    }
                    else {
                        // dua-duanya kosong atau dua-duanya isi
                        System.out.println("[Ground] Tidak ada aksi cocok (hand=" +
                                           (hand == null ? "null" : hand.getName()) +
                                           ", ground=" + (ground == null ? "null" : ground.getName()) + ")");
                    }
                }
            } else {
                System.out.println("[GUI] Tile depan di luar map.");
            }
        }
        wasCPressed = keyH.cPressed;

        // === USE STATION: tombol V ===
        if (keyH.vPressed && !wasVPressed) {

            if (stationInFront == null) {
                System.out.println("Tidak ada station di depan chef.");
            } else {
                System.out.println(">> Station in front: "
                        + stationInFront.getClass().getSimpleName()
                        + " at (" + stationInFront.getPosX() + "," + stationInFront.getPosY() + ")");

                if (stationInFront instanceof AssemblyStation) {
                    System.out.println("[GUI] Untuk plating di Assembly, gunakan tombol C"); 
                } else if (stationInFront instanceof CookingStation) {
                    System.out.println("[GUI] Untuk menggunakan oven (masuk/keluar pizza), gunakan tombol C.");
                } else if (stationInFront instanceof WashingStation) {
                    boolean ok = new WashingAction().execute(activeChef, stationInFront);
                    if (!ok) System.out.println("WashingAction gagal.");
                } else if (stationInFront instanceof TrashStation) {
                    System.out.println("[GUI] Untuk membuang item, gunakan tombol C (Drop).");
                } else if (stationInFront instanceof PlateStorage ps) {
                    boolean ok = ps.interact(activeChef);
                    if (!ok) System.out.println("[PlateStorage] Interaksi gagal (cek: plate bersih / jarak / stack kosong).");
                } else if (stationInFront instanceof ServingCounter) {
                    System.out.println("[GUI] Untuk menyajikan hidangan, gunakan tombol C (Drop).");
                } else {
                    boolean ok = stationInFront.interact(activeChef);
                    if (!ok) System.out.println("Interaksi dengan " 
                            + stationInFront.getStationType() + " gagal.");
                }
            }
        }
        wasVPressed = keyH.vPressed;
    }

    // ==========================================
    // Gambar item di atas semua station
    // ==========================================
    // ... (sisa kode tidak berubah)
    private void drawItemsOnStations(Graphics2D g2) {
        for (Station st : stationMap.values()) {
            Item item = st.getItemOnStation();
            if (item == null) continue; // station kosong → skip

            int px = st.getPosX() * TILE_SIZE - mapOffsetX;
            int py = st.getPosY() * TILE_SIZE - mapOffsetY;

            // === CASE KHUSUS: AssemblyStation + Plate → pakai renderer pizza ===
            if (st instanceof AssemblyStation) {
                if (item instanceof Plate plate) {
                    assemblyRenderer.drawPlateOnAssembly(g2, px, py, TILE_SIZE, plate);
                    continue;
                } else if (item instanceof Dough dough) {
                    assemblyRenderer.drawDoughOnAssembly(g2, px, py, TILE_SIZE, dough);
                    continue;
                }
            }

            // === INGREDIENT DI ATAS STATION (termasuk CuttingStation) → pakai sprite ===
            if (item instanceof Ingredient ing) {
                BufferedImage sprite = assemblyRenderer.getSpriteForIngredient(ing);
                if (sprite != null) {
                    int size  = (int) (TILE_SIZE * 0.8);
                    int drawX = px + TILE_SIZE / 2 - size / 2;
                    int drawY = py + TILE_SIZE / 2 - size / 2;
                    g2.drawImage(sprite, drawX, drawY, size, size, null);
                    continue; // sudah digambar, lanjut station berikutnya
                }
            }

            // === DEFAULT: kotak kecil seperti sebelumnya ===
            int size   = TILE_SIZE - 8;
            int offset = 4;

            Color fillColor = new Color(240, 240, 240);
            String label = "?";

            if (item instanceof Plate plate) {
                fillColor = plate.isClean()
                        ? new Color(245, 245, 255)
                        : new Color(200, 200, 220);
                label = "Pl";
            } else if (item instanceof Ingredient ing) {
                fillColor = new Color(255, 230, 180);
                String name = ing.getName();
                label = name.isEmpty() ? "I" : name.substring(0, 1);
            } else if (item instanceof Dish) {
                fillColor = new Color(255, 200, 200);
                label = "D";
            } else {
                String name = item.getName();
                label = name.substring(0, Math.min(2, name.length()));
            }

            g2.setColor(fillColor);
            g2.fillRoundRect(px + offset, py + offset, size, size, 6, 6);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(px + offset, py + offset, size, size, 6, 6);

            g2.drawString(label, px + offset + 3, py + offset + 12);
        }
    }


    // ==========================================
    // RENDER
    // ==========================================
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

                    TileType tileType = pizzaMap.getTileAt(x, y).getType();

                    if (tileType == TileType.INGREDIENT_STORAGE) {
                        Station st = stationMap.get(stationKey(x, y));
                        if (st instanceof IngredientStorage is) {
                            tileRenderer.drawIngredientStorage(
                                g2,
                                is,
                                screenX, screenY,
                                TILE_SIZE
                            );
                            continue; // lanjut ke tile berikutnya, jangan panggil drawTile lagi
                        }
                    }
                    tileRenderer.drawTile(
                        g2,
                        pizzaMap,
                        tileType,
                        x, y,
                        screenX, screenY,
                        TILE_SIZE
                    );

                    //  TULIS jumlah plate di atas tile P 
                    if (pizzaMap.getTileAt(x, y).getType() == TileType.PLATE_STORAGE) {
                        Station st = stationMap.get(stationKey(x, y));
                        if (st instanceof PlateStorage ps) {
                            String text = String.valueOf(ps.getPlateCount());
                            g2.setColor(Color.BLACK);
                            g2.drawString(text,
                                screenX + TILE_SIZE/2 - 4,
                                screenY + TILE_SIZE/2 + 4);
                        }
                    }

                    if (tileType == TileType.CUTTING_STATION) {
                        Station st = stationMap.get(stationKey(x, y));
                        if (st instanceof CuttingStation cs) {
                            double ratio = cs.getProgressRatio(); // 0.0 .. 1.0
                            if (ratio > 0) {
                                int barWidth  = TILE_SIZE - 4;
                                int barHeight = 4;
                                int bx = screenX + 2;
                                int by = screenY + TILE_SIZE - barHeight - 2;

                                // border
                                g2.setColor(Color.DARK_GRAY);
                                g2.drawRect(bx, by, barWidth, barHeight);

                                // fill
                                int fillWidth = (int) (barWidth * ratio);
                                g2.setColor(Color.GREEN);
                                g2.fillRect(bx + 1, by + 1, fillWidth - 1, barHeight - 1);
                            }
                        }
                    }

                    if (tileType == TileType.COOKING_STATION) {
                        Station st = stationMap.get(stationKey(x, y));
                        if (st instanceof CookingStation cs) {

                            // 1) Gambar oven sesuai state + pola light/dark
                            tileRenderer.drawOven(
                                g2,
                                x, y,
                                screenX, screenY,
                                TILE_SIZE,
                                cs.getOven()
                            );

                            // 2) (opsional) progress bar di atas oven
                            double ratio = cs.getOven().getProgressRatio(); // 0..1 (12 detik)
                            if (ratio > 0) {
                                int barWidth  = TILE_SIZE - 4;
                                int barHeight = 4;
                                int bx = screenX + 2;
                                int by = screenY + TILE_SIZE - barHeight - 2;

                                g2.setColor(Color.DARK_GRAY);
                                g2.drawRect(bx, by, barWidth, barHeight);

                                int fillWidth = (int) (barWidth * ratio);
                                g2.setColor(cs.getOven().isBurned() ? Color.RED : Color.ORANGE);
                                g2.fillRect(bx + 1, by + 1, fillWidth - 1, barHeight - 1);
                            }
                        }
                    }

                    if (tileType == TileType.TRASH) {
                        Station st = stationMap.get(stationKey(x, y));
                        if (st instanceof TrashStation ts) {
                            tileRenderer.drawTrash(
                                g2,
                                screenX, screenY,
                                TILE_SIZE,
                                ts.isFull()
                            );
                        }
                    }

                    // === PROGRESS BAR UNTUK WASHING STATION ===
                                        // === WASHING STATION (sprite + progress bar) ===
                    if (tileType == TileType.WASHING_STATION) {
                        Station st = stationMap.get(stationKey(x, y));
                        if (st instanceof WashingStation ws) {

                            // 1) gambar sprite washing station (empty / washing) + light/dark
                            tileRenderer.drawWashing(
                                g2,
                                x, y,
                                screenX, screenY,
                                TILE_SIZE,
                                ws.isWashing()
                            );

                            // 2) progress bar di atas sink (opsional, sama kayak sebelumnya)
                            double ratio = ws.getProgressRatio(); // 0..1
                            if (ratio > 0) {
                                int barWidth  = TILE_SIZE - 4;
                                int barHeight = 4;
                                int bx = screenX + 2;
                                int by = screenY + TILE_SIZE - barHeight - 2;

                                g2.setColor(Color.DARK_GRAY);
                                g2.drawRect(bx, by, barWidth, barHeight);

                                int fillWidth = (int) (barWidth * ratio);
                                g2.setColor(Color.CYAN);
                                g2.fillRect(bx + 1, by + 1, fillWidth - 1, barHeight - 1);
                            }
                        }
                    }
                }
            }
        }
        // INI NAANTI DIHPUAPUS AKU MAU CEK SEMUA ITEM AJA
        drawItemsOnStations(g2);

        for (var entry : groundItems.entrySet()) {
            String[] parts = entry.getKey().split(",");
            int gx = Integer.parseInt(parts[0]);
            int gy = Integer.parseInt(parts[1]);
            Item item = entry.getValue();

            int px = gx * TILE_SIZE - mapOffsetX;
            int py = gy * TILE_SIZE - mapOffsetY;

            // --- kalau ingredient pizza → pakai sprite yang sama kayak di plate ---
            if (item instanceof Ingredient ing) {
                BufferedImage sprite = assemblyRenderer.getSpriteForIngredient(ing);
                if (sprite != null) {
                    int size = (int) (TILE_SIZE * 0.8);
                    int drawX = px + TILE_SIZE / 2 - size / 2;
                    int drawY = py + TILE_SIZE / 2 - size / 2;
                    g2.drawImage(sprite, drawX, drawY, size, size, null);
                    continue; // sudah digambar, lanjut item berikutnya
                }
            }

            // --- fallback: kotak abu + label (buat item lain) ---
            int size   = TILE_SIZE - 10;
            int offset = 5;

            g2.setColor(new Color(220, 220, 220));
            g2.fillRoundRect(px + offset, py + offset, size, size, 6, 6);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(px + offset, py + offset, size, size, 6, 6);

            String label = item.getName().substring(0, Math.min(2, item.getName().length()));
            g2.drawString(label, px + offset + 4, py + offset + 12);
        }

        // gambar chef 1
        int chef1X = chef1.getPosition().getX() * TILE_SIZE - mapOffsetX;
        int chef1Y = chef1.getPosition().getY() * TILE_SIZE - mapOffsetY;
        chef1Sprite.draw(g2, chef1X, chef1Y, TILE_SIZE, isPlayer1Active);
        drawHeldItemIcon(g2, chef1, chef1X, chef1Y);

        // gambar chef 2
        int chef2X = chef2.getPosition().getX() * TILE_SIZE - mapOffsetX;
        int chef2Y = chef2.getPosition().getY() * TILE_SIZE - mapOffsetY;
        chef2Sprite.draw(g2, chef2X, chef2Y, TILE_SIZE, !isPlayer1Active);
        drawHeldItemIcon(g2, chef2, chef2X, chef2Y); 

        g2.setColor(Color.WHITE);
        g2.drawString("Active: " + (isPlayer1Active ? chef1.getName() : chef2.getName()) + " (WASD)", 10, 20);
        g2.drawString("TAB: Switch | C: Drop / Serve / Trash | V: Use Station | E: Throw | Shift+WASD: Dash", 10, 40);

        int score = model.manager.ScoreManager.getInstance().getScore();
        g2.drawString("Score: " + score, 10, 60);

        // ====== Gambar daftar Order aktif dengan template 2x2 blok di pojok kanan atas ======
        drawOrderUI(g2);

        drawSettingButton(g2);
        drawBottomStatusBar(g2);
        int dashY = SCREEN_HEIGHT - 3 * TILE_SIZE - 10;
        g2.setFont(g2.getFont().deriveFont(11f));

        String chef1Dash = "Chef1 Dash: " + dashController.getRemainingCooldownString(true);
        g2.setColor(dashController.canDash(true) ? Color.GREEN : Color.RED);
        g2.drawString(chef1Dash, 10, dashY);

        String chef2Dash = "Chef2 Dash: " + dashController.getRemainingCooldownString(false);
        g2.setColor(dashController.canDash(false) ? Color.GREEN : Color.RED);
        g2.drawString(chef2Dash, 10, dashY + 15);

        // Display Throw Distance
        g2.setColor(Color.WHITE);
    }

    private void drawSettingButton(Graphics2D g2) {
        int buttonSize = 2 * TILE_SIZE;
        settingButtonX = SCREEN_WIDTH - buttonSize;
        settingButtonY = 6 * TILE_SIZE + 10; // diturunkan sedikit
        settingButtonWidth = buttonSize;
        settingButtonHeight = buttonSize;

        if (settingsIconImage != null) {
            g2.drawImage(settingsIconImage, settingButtonX, settingButtonY, buttonSize, buttonSize, null);
        } else {
            g2.setColor(new Color(250, 250, 250, 220));
            g2.fillRoundRect(settingButtonX, settingButtonY, buttonSize, buttonSize, 8, 8);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(settingButtonX, settingButtonY, buttonSize, buttonSize, 8, 8);
        }
    }

    private String getChefHeldItemInfo(Chef chef) {
        Item item = chef.getHeldItem();
        if (item == null) {
            return "Empty";
        }
        
        if (item instanceof Plate plate) {
            int count = plate.getContents().size();
            return count == 0 ? "Plate" : "Plate(" + count + ")";
        } else if (item instanceof Ingredient) {
            return item.getName();
        } else if (item instanceof Dish) {
            return "Dish";
        } else {
            return item.getName();
        }
    }

    private String getRemainingTimeFormatted() {
        int totalSeconds = (int) (model.manager.OrderManager.getSessionLimitSeconds() -
                                   model.manager.OrderManager.getInstance().getSessionTimeElapsed());
        if (totalSeconds < 0) totalSeconds = 0;
        
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        
        return String.format("%d:%02d", minutes, seconds);
    }

    private void drawBottomStatusBar(Graphics2D g2) {
        int barHeight = 2 * TILE_SIZE;
        int barY = SCREEN_HEIGHT - barHeight;

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, barY, SCREEN_WIDTH, barHeight);

        int inventoryWidth = 4 * TILE_SIZE;
        int inventoryHeight = barHeight;

        int chef1InvX = TILE_SIZE / 2;
        int chef1InvY = barY;
        int chef2InvX = chef1InvX + inventoryWidth + TILE_SIZE / 2;
        int chef2InvY = barY;

        if (chefInventory1Image != null) {
            g2.drawImage(chefInventory1Image, chef1InvX, chef1InvY, inventoryWidth, inventoryHeight, null);
        }
        if (chefInventory2Image != null) {
            g2.drawImage(chefInventory2Image, chef2InvX, chef2InvY, inventoryWidth, inventoryHeight, null);
        }

        int iconSize = TILE_SIZE;

        int chef1CenterX = chef1InvX + inventoryWidth / 2;
        int chef2CenterX = chef2InvX + inventoryWidth / 2;

        // gambar item yang dipegang chef di atas panel inventory bawah (pakai sprite)
        drawInventoryHeldItemIcon(g2, chef1, chef1CenterX, barY + barHeight);
        drawInventoryHeldItemIcon(g2, chef2, chef2CenterX, barY + barHeight);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(12f));

        int countdownBoxWidth = 6 * TILE_SIZE;
        int countdownBoxHeight = barHeight - TILE_SIZE / 3;
        int countdownBoxX = chef2InvX + inventoryWidth + TILE_SIZE / 2;
        int countdownBoxY = barY + (barHeight - countdownBoxHeight) / 2;
        
        Color woodBrown = new Color(101, 67, 33);
        Color burntWood = new Color(139, 69, 19);
        
        g2.setColor(burntWood);
        g2.fillRoundRect(countdownBoxX, countdownBoxY, countdownBoxWidth, countdownBoxHeight, 5, 5);
        
        g2.setColor(woodBrown);
        g2.drawRoundRect(countdownBoxX, countdownBoxY, countdownBoxWidth, countdownBoxHeight, 5, 5);
        
        String countdownText = getRemainingTimeFormatted();
        g2.setFont(g2.getFont().deriveFont(32f));
        g2.setColor(Color.WHITE);
        
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(countdownText);
        int textHeight = fm.getHeight();
        int textX = countdownBoxX + (countdownBoxWidth - textWidth) / 2;
        int textY = countdownBoxY + (countdownBoxHeight + textHeight) / 2 - fm.getDescent();
        
        g2.drawString(countdownText, textX, textY);
        
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.drawString("LOGO", countdownBoxX + countdownBoxWidth + TILE_SIZE / 2, barY + barHeight / 2);
    }

    /**
     * Menggambar tampilan order dengan template 2x2 blok di pojok kanan atas
     * Setiap blok berukuran 2x2 tile, disusun vertikal ke bawah
     */
    private void drawOrderUI(Graphics2D g2) {
        var orders = OrderManager.getInstance().getActiveOrders();

        // Ukuran blok 2x2 tile
        int blockSize = TILE_SIZE * 2;

        // Posisi awal di pojok kanan atas, benar-benar menempel ke kanan
        int startX = SCREEN_WIDTH - blockSize; // tanpa margin kiri-kanan
        int startY = 0;                        // mentok ke atas

        // Jarak antar blok secara vertikal (diperkecil agar kertas tampak lebih rapat)
        int blockSpacing = TILE_SIZE / 6;

        // Font dasar untuk text di dalam blok
        g2.setFont(g2.getFont().deriveFont(9f));

        // Hanya tampilkan maksimal 3 order (3 blok 2x2)
        int maxBlocks = Math.min(3, orders.size());
        for (int i = 0; i < maxBlocks; i++) {
            var order = orders.get(i);
            int x = startX;
            int y = startY + i * (blockSize + blockSpacing);

            drawOrderBlock(g2, x, y, blockSize, order, i);
        }
    }
    
    /**
     * Menggambar satu blok order berukuran 2x2 tile
     */
    private void drawOrderBlock(Graphics2D g2, int x, int y, int blockSize, model.item.dish.Order order, int index) {
        // Pilih background kertas berdasarkan sisa waktu global sesi:
        // 80-52 detik  -> KertasOrderan.png (order pertama)
        // 51-26 detik  -> KertasOrderan2.png (order kedua)
        // 25-0 detik   -> KertasOrderan3.png (order ketiga)
        int globalRemaining = (int) (model.manager.OrderManager.getSessionLimitSeconds() -
                                     model.manager.OrderManager.getInstance().getSessionTimeElapsed());
        if (globalRemaining < 0) globalRemaining = 0;

        BufferedImage bg;
        if (globalRemaining <= 80 && globalRemaining >= 52) {
            bg = orderPaper1;
        } else if (globalRemaining <= 51 && globalRemaining >= 26) {
            bg = orderPaper2;
        } else if (globalRemaining <= 25 && globalRemaining >= 0) {
            bg = orderPaper3;
        } else {
            bg = orderPaper1;
        }

        if (bg != null) {
            g2.drawImage(bg, x, y, blockSize, blockSize, null);
        } else {
            g2.setColor(new Color(245, 245, 220));
            g2.fillRoundRect(x, y, blockSize, blockSize, 12, 12);
        }

        drawOrderInfo(g2, x, y, blockSize, order);
    }
    
    /**
     * Menggambar informasi order di dalam blok, termasuk daftar ingredients
     * dan penanda (chopped) jika perlu dipotong.
     */
    private void drawOrderInfo(Graphics2D g2, int x, int y, int blockSize, model.item.dish.Order order) {
        String orderText = String.format("#%d", order.getId());
        String pizzaText = order.getPizzaType().getDisplayName();
        String timeText = String.format("%ds", order.getTimeRemaining());

        java.awt.FontMetrics fm = g2.getFontMetrics();

        // Area dalam blok (beri sedikit padding dari border)
        int padding = 6;
        int innerX = x + padding;
        int innerY = y + padding;
        int innerWidth = blockSize - padding * 2;

        int lineHeight = fm.getHeight();
        int cursorY = innerY + lineHeight; // mulai dari baris pertama di dalam blok

        // Order ID di baris pertama
        int orderX = innerX + (innerWidth - fm.stringWidth(orderText)) / 2;
        drawTextWithShadow(g2, orderText, orderX, cursorY, Color.BLACK, Color.LIGHT_GRAY);

        // Nama pizza di baris kedua
        cursorY += lineHeight;
        int pizzaX = innerX + (innerWidth - fm.stringWidth(pizzaText)) / 2;
        drawTextWithShadow(g2, pizzaText, pizzaX, cursorY, Color.BLACK, Color.LIGHT_GRAY);

        // Daftar ingredients di bawah nama pizza
        cursorY += lineHeight;
        g2.setFont(g2.getFont().deriveFont(8f));
        fm = g2.getFontMetrics();
        lineHeight = fm.getHeight();

        // Judul kecil "Ingredients:"
        String ingTitle = "Ingredients:";
        drawTextWithShadow(g2, ingTitle, innerX, cursorY, Color.BLACK, Color.LIGHT_GRAY);

        // Tentukan ingredients berdasarkan jenis pizza
        java.util.List<String> ingredientLines = new java.util.ArrayList<>();
        switch (order.getPizzaType()) {
            case MARGHERITA -> {
                ingredientLines.add("Adonan (chopped)");
                ingredientLines.add("Tomat (chopped)");
                ingredientLines.add("Keju (chopped)");
            }
            case SOSIS -> {
                ingredientLines.add("Adonan (chopped)");
                ingredientLines.add("Tomat (chopped)");
                ingredientLines.add("Keju (chopped)");
                ingredientLines.add("Sosis (chopped)");
            }
            case AYAM -> {
                ingredientLines.add("Adonan (chopped)");
                ingredientLines.add("Tomat (chopped)");
                ingredientLines.add("Keju (chopped)");
                ingredientLines.add("Ayam (chopped)");
                ingredientLines.add("Masak di Oven");
            }
        }

        // Gambar setiap baris ingredients, dibatasi supaya muat di dalam blok
        for (String line : ingredientLines) {
            cursorY += lineHeight;
            // sisakan 1 baris untuk teks waktu di bawah
            if (cursorY > y + blockSize - lineHeight * 1) {
                break;
            }
            drawTextWithShadow(g2, "- " + line, innerX, cursorY, Color.BLACK, Color.LIGHT_GRAY);
        }

        // Waktu di baris terakhir, ditempatkan di bagian bawah blok
        g2.setFont(g2.getFont().deriveFont(9f));
        fm = g2.getFontMetrics();
        timeText = String.format("Time: %ds", order.getTimeRemaining());
        int timeX = innerX + (innerWidth - fm.stringWidth(timeText)) / 2;
        int timeY = y + blockSize - padding;
        drawTextWithShadow(g2, timeText, timeX, timeY, Color.BLACK, Color.LIGHT_GRAY);
    }
    
    /**
     * Menggambar text dengan shadow effect
     */
    private void drawTextWithShadow(Graphics2D g2, String text, int x, int y, Color textColor, Color shadowColor) {
        // Shadow
        g2.setColor(shadowColor);
        g2.drawString(text, x + 1, y + 1);
        
        // Main text
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }
}

//WARNING KALO KALIAN MAU GANTI INI 
//CEK DULU APAKAH FUNGSI KALIAN GANTI BEKERJA ATAU GAK
//KALAU MAU PISAHIN SILAHKAN TPI PASTIIN GAK ADA YANG ILANG