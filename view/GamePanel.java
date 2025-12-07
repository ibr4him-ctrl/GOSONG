package view;

import actions.useStation.AssemblyAction;
import actions.useStation.CookingAction;
import actions.useStation.WashingAction;
import controller.PickUpDrop;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
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


public class GamePanel extends JPanel implements Runnable {

    // setting layar
    private static final int ORIGINAL_TILE_SIZE = 16;
    private static final int SCALE = 3;
    private static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    private static final int MAX_SCREEN_COL = 16;
    private static final int MAX_SCREEN_ROW = 12;
    private static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    private static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

    // map -> Station
    private Map<String, Station> stationMap = new HashMap<>();

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

    // flags tombol
    private boolean wasTabPressed = false;
    private boolean wasCPressed = false;
    private boolean wasVPressed = false;

    private long lastUpdateNs = System.nanoTime();

    public GamePanel() {

        pizzaMap = new PizzaMap();
        initStationsFromMap();  // <-- bangun objek Station dari PizzaMap
        initPlateStorages();

        // Inisialisasi chef dari spawnpoint
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

        chef1Sprite = new PlayerSprite();
        chef2Sprite = new PlayerSprite();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);

        // INI ORDER SYSTEM 
        OrderManager.getInstance().init();
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

        // RESET SCORE DI AWAL GAME
        model.manager.ScoreManager.getInstance().reset();
    }

    private Chef getActiveChef() {
        return isPlayer1Active ? chef1 : chef2;
    }

    private PlayerSprite getActiveSprite() {
        return isPlayer1Active ? chef1Sprite : chef2Sprite;
    }

    private Station getStationInFrontOf(Chef chef, PlayerSprite sprite) {
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

        return stationMap.get(stationKey(tx, ty));
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


    // ==========================================
    // HANDLE ACTION (C & V)
    // ==========================================
private void handleActions() {
    Chef activeChef = getActiveChef();
    PlayerSprite activeSprite = getActiveSprite();
    Station stationInFront = getStationInFrontOf(activeChef, activeSprite);

    // === PICK UP / DROP: tombol C ===
    if (keyH.cPressed && !wasCPressed) {
        System.out.println("[GUI] C pressed by " + activeChef.getName());
        boolean ok = new PickUpDrop().execute(activeChef, stationInFront);
        if (!ok) {
            System.out.println("PickUpDrop gagal atau tidak ada station cocok.");
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
                boolean ok = new AssemblyAction().execute(activeChef, stationInFront);
                if (!ok) System.out.println("AssemblyAction gagal.");
            } else if (stationInFront instanceof CookingStation) {
                boolean ok = new CookingAction().execute(activeChef, stationInFront);
                if (!ok) System.out.println("CookingAction gagal.");
            } else if (stationInFront instanceof WashingStation) {
                boolean ok = new WashingAction().execute(activeChef, stationInFront);
                if (!ok) System.out.println("WashingAction gagal.");
            }else {
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
    private void drawItemsOnStations(Graphics2D g2) {
        for (Station st : stationMap.values()) {
            Item item = st.getItemOnStation();
            if (item == null) continue; // station kosong → skip

            int px = st.getPosX() * TILE_SIZE - mapOffsetX;
            int py = st.getPosY() * TILE_SIZE - mapOffsetY;

            int size   = TILE_SIZE - 8;
            int offset = 4;

            // Pilih warna & label berdasarkan tipe item
            Color fillColor = new Color(240, 240, 240);
            String label = "?";

            if (item instanceof Plate plate) {
                // plate bersih / kotor beda dikit warnanya
                fillColor = plate.isClean()
                        ? new Color(245, 245, 255)
                        : new Color(200, 200, 220);
                label = "Pl";
            } else if (item instanceof Ingredient ing) {
                fillColor = new Color(255, 230, 180);
                // ambil huruf pertama nama ingredient (T / C / D / S / dsb)
                String name = ing.getName();
                label = name.isEmpty() ? "I" : name.substring(0, 1);
            } else if (item instanceof Dish) {
                fillColor = new Color(255, 200, 200);
                label = "D";
            } else {
                // fallback: 2 huruf pertama nama item
                String name = item.getName();
                label = name.substring(0, Math.min(2, name.length()));
            }

            // kotak kecil di tengah tile
            g2.setColor(fillColor);
            g2.fillRoundRect(px + offset, py + offset, size, size, 6, 6);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(px + offset, py + offset, size, size, 6, 6);

            // tulis label
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
                            double ratio = cs.getOven().getProgressRatio(); // 0..1 (12 detik)
                            if (ratio > 0) {
                                int barWidth  = TILE_SIZE - 4;
                                int barHeight = 4;
                                int bx = screenX + 2;
                                int by = screenY + TILE_SIZE - barHeight - 2;

                                // border
                                g2.setColor(Color.DARK_GRAY);
                                g2.drawRect(bx, by, barWidth, barHeight);

                                // fill: oranye kalau normal, merah kalau burned
                                int fillWidth = (int) (barWidth * ratio);
                                g2.setColor(cs.getOven().isBurned() ? Color.RED : Color.ORANGE);
                                g2.fillRect(bx + 1, by + 1, fillWidth - 1, barHeight - 1);
                            }
                        }
                    }
                    // === PROGRESS BAR UNTUK WASHING STATION ===
                    if (tileType == TileType.WASHING_STATION) {
                        Station st = stationMap.get(stationKey(x, y));
                        if (st instanceof WashingStation ws) {
                            double ratio = ws.getProgressRatio(); // 0..1
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
                                g2.setColor(Color.CYAN);      // warna bebas
                                g2.fillRect(bx + 1, by + 1, fillWidth - 1, barHeight - 1);
                            }
                        }
                    }
                }
            }
        }
        // INI NAANTI DIHPUAPUS AKU MAU CEK SEMUA ITEM AJA
        drawItemsOnStations(g2);


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
        g2.drawString("TAB: Switch | C: PickUp/Drop | V: Use Station", 10, 40);

        int score = model.manager.ScoreManager.getInstance().getScore();
        g2.drawString("Score: " + score, 10, 60);

        // ====== Gambar daftar Order aktif di pojok kanan atas ======
        var orders = OrderManager.getInstance().getActiveOrders();
        int boxWidth = 260;
        int boxHeight = 20;
        int startX = SCREEN_WIDTH - boxWidth - 10;
        int startY = 60;

        g2.setFont(g2.getFont().deriveFont(12f));

        int i = 0;
        for (var order : orders) {
            int x = startX;
            int y = startY + i * (boxHeight + 4);

            // background
            g2.setColor(new Color(250, 250, 250, 220));
            g2.fillRoundRect(x, y, boxWidth, boxHeight, 8, 8);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, boxWidth, boxHeight, 8, 8);

            String text = String.format(
                "#%d %s  [%ds]",
                order.getId(),
                order.getPizzaType().getDisplayName(),
                order.getTimeRemaining()
            );
            g2.drawString(text, x + 6, y + 14);

            i++;
        }
        g2.dispose();
    }
}
