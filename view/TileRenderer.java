package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Cheese;
import model.item.ingredient.pizza.Chicken;
import model.item.ingredient.pizza.Dough;
import model.item.ingredient.pizza.Sausage;
import model.item.ingredient.pizza.Tomato;
import model.item.utensils.Oven;
import model.map.PizzaMap;
import model.map.tile.TileType;
import model.station.IngredientStorage;


public class TileRenderer {

    private BufferedImage tileLight;
    private BufferedImage tileDark;
    private BufferedImage spawnTile;

    // station sprites
    private BufferedImage assemblyStationImg;
    private BufferedImage cuttingStationImg;
    private BufferedImage plateStorageImg; 

    // ===== WALL SPRITES =====
    private BufferedImage wallBottom;
    private BufferedImage wallLeft3;
    private BufferedImage wallLeftBottom;
    private BufferedImage wallLeftUpper;
    private BufferedImage wallLeft;
    private BufferedImage wallRight3;
    private BufferedImage wallRightBottom;
    private BufferedImage wallRightUpper;
    private BufferedImage wallRight;
    private BufferedImage wallSolid;
    private BufferedImage wallUpper;
    private BufferedImage wallUpperBottom;


    // ===== COOKING STATION (OVEN) SPRITES =====
    private BufferedImage ovenEmptyLight;
    private BufferedImage ovenEmptyDark;
    private BufferedImage ovenCookingLight;
    private BufferedImage ovenCookingDark;
    private BufferedImage ovenReadyLight;
    private BufferedImage ovenReadyDark;
    private BufferedImage ovenBurntLight;
    private BufferedImage ovenBurntDark;

    // ===== TRASH STATION SPRITES =====
    private BufferedImage trashEmpty;
    private BufferedImage trashFull;

    // ===== WASHING STATION SPRITES =====
    private BufferedImage washEmptyLight;
    private BufferedImage washEmptyDark;
    private BufferedImage washBusyLight;
    private BufferedImage washBusyDark;

    // ===== SERVING COUNTER SPRITES =====
    private BufferedImage servingUpper;
    private BufferedImage servingBottom;


    // ===== INGREDIENT STORAGE SPRITES =====
    private BufferedImage doughStorageImg;
    private BufferedImage tomatoStorageImg;
    private BufferedImage cheeseStorageImg;
    private BufferedImage chickenStorageImg;
    private BufferedImage sausageStorageImg;


    public TileRenderer() {
        loadTiles();
    }

    private void loadTiles() {
        tileLight  = loadImage("resources/tile/tile-light.png");
        tileDark   = loadImage("resources/tile/tile-dark.png");
        spawnTile  = loadImage("resources/tile/spawn-tile.png");

        // station
        assemblyStationImg = loadImage("resources/station/assembling_station/assemblystation2.png");
        cuttingStationImg  = loadImage("resources/station/cutting_station/cutting_station.png");
        plateStorageImg    = loadImage("resources/station/plate_storage/platestorage.png");

        // wall
        wallBottom      = loadImage("resources/wall/bottom-wall.png");
        wallLeft3       = loadImage("resources/wall/left-3-wall.png");
        wallLeftBottom  = loadImage("resources/wall/left-bottom-wall.png");
        wallLeftUpper   = loadImage("resources/wall/left-upper-wall.png");
        wallLeft        = loadImage("resources/wall/left-wall.png");
        wallRight3      = loadImage("resources/wall/right-3-wall.png");
        wallRightBottom = loadImage("resources/wall/right-bottom-wall.png");
        wallRightUpper  = loadImage("resources/wall/right-upper-wall.png");
        wallRight       = loadImage("resources/wall/right-wall.png");
        wallSolid       = loadImage("resources/wall/solid-wall.png");
        wallUpper       = loadImage("resources/wall/upper-wall.png");
        wallUpperBottom = loadImage("resources/wall/upper-bottom-wall.png");

        // ===== OVEN SPRITES =====
        ovenEmptyDark   = loadImage("resources/station/cooking_station/oven-kosong-dark.png");
        ovenEmptyLight  = loadImage("resources/station/cooking_station/oven-kosong-light.png");
        ovenCookingDark = loadImage("resources/station/cooking_station/oven-cooking-dark.png");
        ovenCookingLight= loadImage("resources/station/cooking_station/oven-cooking-light.png");
        ovenReadyDark   = loadImage("resources/station/cooking_station/oven-ready-dark.png");
        ovenReadyLight  = loadImage("resources/station/cooking_station/oven-ready-light.png");
        ovenBurntDark   = loadImage("resources/station/cooking_station/oven-burnt-dark.png");
        ovenBurntLight  = loadImage("resources/station/cooking_station/oven-burnt-light.png");


        // ===== TRASH SPRITES =====
        trashEmpty = loadImage("resources/station/trash_station/trash-empty.png");
        trashFull  = loadImage("resources/station/trash_station/trash-full.png");

        // ===== WASHING STATION SPRITES =====
        washEmptyDark = loadImage("resources/station/washing_station/empty-dark.png");
        washEmptyLight = loadImage("resources/station/washing_station/empty-light.png");
        washBusyDark = loadImage("resources/station/washing_station/wash-dark.png");
        washBusyLight = loadImage("resources/station/washing_station/wash-light.png");

        // ===== SERVING COUNTER SPRITES =====
        servingUpper  = loadImage("resources/station/serving_station/serving-upper.png");
        servingBottom = loadImage("resources/station/serving_station/serving-bottom.png");

        // ===== INGREDIENT STORAGE SPRITES =====
        doughStorageImg   = loadImage("resources/station/ingredient_station/dough_storage.png");
        tomatoStorageImg  = loadImage("resources/station/ingredient_station/tomato_storage.png");
        cheeseStorageImg  = loadImage("resources/station/ingredient_station/cheese_storage.png");
        chickenStorageImg = loadImage("resources/station/ingredient_station/chicken_storage.png");
        sausageStorageImg = loadImage("resources/station/ingredient_station/sausage_storage.png");
    }

    private BufferedImage loadImage(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                System.out.println("[TileRenderer] File not found: " + f.getAbsolutePath());
                return null;
            }
            System.out.println("[TileRenderer] Loaded: " + f.getAbsolutePath());
            return ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("[TileRenderer] Error loading " + path + ": " + e.getMessage());
            return null;
        }
    }

    // ==========================
    // HELPER WALL
    // ==========================
    private boolean hasFloor(PizzaMap map, int x, int y) {
        if (x < 0 || x >= PizzaMap.WIDTH || y < 0 || y >= PizzaMap.HEIGHT) {
            return false; // luar map → bukan floor
        }
        TileType t = map.getTileAt(x, y).getType();
        return t != TileType.WALL;
    }

    private boolean isServingCounter(PizzaMap map, int x, int y) {
        if (x < 0 || x >= PizzaMap.WIDTH || y < 0 || y >= PizzaMap.HEIGHT) {
            return false;
        }
        return map.getTileAt(x, y).getType() == TileType.SERVING_COUNTER;
    }


    private void drawWallTile(Graphics2D g2,
                            PizzaMap map,
                            int xIndex, int yIndex,
                            int screenX, int screenY,
                            int tileSize) {

        boolean up    = hasFloor(map, xIndex,     yIndex - 1);
        boolean down  = hasFloor(map, xIndex,     yIndex + 1);
        boolean left  = hasFloor(map, xIndex - 1, yIndex);
        boolean right = hasFloor(map, xIndex + 1, yIndex);

        BufferedImage tex = null;

        // ==== 3 sisi dulu (paling spesifik) ====
        if (left && up && down && !right) {
            tex = wallLeft3;
        } else if (right && up && down && !left) {
            tex = wallRight3;
        }
        // ==== 2 sisi corner (kiri/bawah, kiri/atas, kanan/bawah, kanan/atas) ====
        else if (left && down && !right) {
            tex = wallLeftBottom;
        } else if (left && up && !right) {
            tex = wallLeftUpper;
        } else if (right && down && !left) {
            tex = wallRightBottom;
        } else if (right && up && !left) {
            tex = wallRightUpper;
        }
        // ==== 2 sisi vertikal (atas + bawah saja) ====
        else if (up && down && !left && !right) {
            tex = wallUpperBottom;
        }
        // ==== 1 sisi utama ====
        else if (down) {          // ada lantai di bawah → pakai bottom-wall
            tex = wallBottom;
        } else if (up) {          // ada lantai di atas → pakai upper-wall
            tex = wallUpper;
        } else if (left) {
            tex = wallLeft;
        } else if (right) {
            tex = wallRight;
        }
        // ==== tidak nempel lantai di manapun → blok penuh ====
        else {
            tex = wallSolid;
        }

        if (tex != null) {
            g2.drawImage(tex, screenX, screenY, tileSize, tileSize, null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(screenX, screenY, tileSize, tileSize);
        }
    }

    public void drawOven(Graphics2D g2,
                        int xIndex, int yIndex,
                        int screenX, int screenY,
                        int tileSize,
                        Oven oven) {

        // ikut pola lantai: genap = light, ganjil = dark (sama kayak WALKABLE)
        boolean isLight = ((xIndex + yIndex) % 2 == 0);

        BufferedImage tex = null;

        if (oven == null) {
            // fallback: kosong
            tex = isLight ? ovenEmptyLight : ovenEmptyDark;
        } else if (oven.isBurned()) {
            tex = isLight ? ovenBurntLight : ovenBurntDark;
        } else if (oven.isReadyToTakeOut()) {
            // sudah matang tapi belum gosong
            tex = isLight ? ovenReadyLight : ovenReadyDark;
        } else if (oven.isCooking()) {
            tex = isLight ? ovenCookingLight : ovenCookingDark;
        } else if (oven.isEmpty()) {
            tex = isLight ? ovenEmptyLight : ovenEmptyDark;
        } else {
            // default (misal ada isi tapi belum startCooking)
            tex = isLight ? ovenEmptyLight : ovenEmptyDark;
        }

        if (tex != null) {
            g2.drawImage(tex, screenX, screenY, tileSize, tileSize, null);
        } else {
            // kalau ada gambar yang gagal ke-load, fallback kotak
            g2.setColor(Color.RED);
            g2.fillRect(screenX, screenY, tileSize, tileSize);
        }
    }

    public void drawTrash(Graphics2D g2,
                        int screenX, int screenY,
                        int tileSize,
                        boolean isFull) {

        BufferedImage tex = isFull ? trashFull : trashEmpty;

        if (tex != null) {
            g2.drawImage(tex, screenX, screenY, tileSize, tileSize, null);
        } else {
            g2.setColor(new Color(128, 0, 0));
            g2.fillRect(screenX, screenY, tileSize, tileSize);
        }
    }

    public void drawWashing(Graphics2D g2,
                            int xIndex, int yIndex,
                            int screenX, int screenY,
                            int tileSize,
                            boolean isWashing) {

        // pola lantai sama kayak WALKABLE
        boolean isLight = ((xIndex + yIndex) % 2 == 0);

        BufferedImage tex;
        if (isWashing) {
            tex = isLight ? washBusyLight : washBusyDark;
        } else {
            tex = isLight ? washEmptyLight : washEmptyDark;
        }

        if (tex != null) {
            g2.drawImage(tex, screenX, screenY, tileSize, tileSize, null);
        } else {
            // fallback kalau sprite gagal ke-load
            g2.setColor(Color.BLUE);
            g2.fillRect(screenX, screenY, tileSize, tileSize);
        }
    }

    public void drawIngredientStorage(Graphics2D g2,
                                    IngredientStorage storage,
                                    int screenX, int screenY,
                                    int tileSize) {

        if (storage == null) {
            g2.setColor(Color.YELLOW);
            g2.fillRect(screenX, screenY, tileSize, tileSize);
            return;
        }

        Class<? extends Ingredient> type = storage.getIngredientType();
        BufferedImage tex = null;

        if (type == Dough.class) {
            tex = doughStorageImg;
        } else if (type == Tomato.class) {
            tex = tomatoStorageImg;
        } else if (type == Cheese.class) {
            tex = cheeseStorageImg;
        } else if (type == Chicken.class) {
            tex = chickenStorageImg;
        } else if (type == Sausage.class) {
            tex = sausageStorageImg;
        }

        if (tex != null) {
            g2.drawImage(tex, screenX, screenY, tileSize, tileSize, null);
        } else {
            // fallback kalau ada yang gak ke-load
            g2.setColor(Color.YELLOW);
            g2.fillRect(screenX, screenY, tileSize, tileSize);
        }
    }


    /**
     * Gambar 1 tile di (screenX, screenY) dengan ukuran tileSize.
     * xIndex,yIndex = indeks tile di map (0..WIDTH-1, 0..HEIGHT-1)
     */
    public void drawTile(Graphics2D g2,
                         PizzaMap map,
                         TileType type,
                         int xIndex, int yIndex,
                         int screenX, int screenY,
                         int tileSize) {

        // =============== SPAWN TILE ===============
        if (type == TileType.SPAWN_CHEF) {
            if (spawnTile != null) {
                g2.drawImage(spawnTile, screenX, screenY, tileSize, tileSize, null);
            } else {
                g2.setColor(Color.GREEN);
                g2.fillRect(screenX, screenY, tileSize, tileSize);
            }
            return;
        }

        // =============== FLOOR CHECKERBOARD (WALKABLE) ===============
        if (type == TileType.WALKABLE) {
            BufferedImage tex =
                    ((xIndex + yIndex) % 2 == 0) ? tileLight : tileDark;

            if (tex != null) {
                g2.drawImage(tex, screenX, screenY, tileSize, tileSize, null);
            } else {
                Color c = ((xIndex + yIndex) % 2 == 0)
                        ? new Color(220, 220, 220)
                        : new Color(200, 200, 200);
                g2.setColor(c);
                g2.fillRect(screenX, screenY, tileSize, tileSize);
            }
            return;
        }

        // =============== WALL AUTOTILE ===============
        if (type == TileType.WALL) {
            drawWallTile(g2, map, xIndex, yIndex, screenX, screenY, tileSize);
            return;
        }

        // =============== TILE LAIN (STATION, DLL) ===============
        if (type == TileType.ASSEMBLY_STATION && assemblyStationImg != null) {
                g2.drawImage(assemblyStationImg, screenX, screenY, tileSize, tileSize, null);
                return;
            }

            if (type == TileType.CUTTING_STATION && cuttingStationImg != null) {
                g2.drawImage(cuttingStationImg, screenX, screenY, tileSize, tileSize, null);
                return;
            }

            if (type == TileType.SERVING_COUNTER) {

                boolean hasAbove = isServingCounter(map, xIndex, yIndex - 1);
                boolean hasBelow = isServingCounter(map, xIndex, yIndex + 1);

                BufferedImage tex = null;

                // kalau di bawahnya masih serving → ini tile atas
                if (!hasAbove && hasBelow) {
                    tex = servingUpper;
                }
                // kalau di atasnya masih serving → ini tile bawah
                else if (hasAbove && !hasBelow) {
                    tex = servingBottom;
                }
                // fallback (misalnya cuma 1 tile doang)
                else {
                    tex = servingBottom != null ? servingBottom : servingUpper;
                }

                if (tex != null) {
                    g2.drawImage(tex, screenX, screenY, tileSize, tileSize, null);
                } else {
                    // kalau gambar gagal ke-load, jangan crash
                    g2.setColor(Color.MAGENTA);
                    g2.fillRect(screenX, screenY, tileSize, tileSize);
                }
                return;
            }
            if (type == TileType.PLATE_STORAGE && plateStorageImg != null) {
                g2.drawImage(plateStorageImg, screenX, screenY, tileSize, tileSize, null);
                return;
            }
        g2.fillRect(screenX, screenY, tileSize, tileSize);
    }
}
