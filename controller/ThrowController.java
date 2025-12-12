package controller;

import model.chef.Chef;
import model.item.Item;
import model.item.ingredient.Ingredient;
import model.map.Position;
import model.map.tile.Tile;
import model.map.tile.TileType; // Make sure to import TileType

import java.util.ArrayList;
import java.util.List;

public class ThrowController {
    
    private static final int MIN_THROW_DISTANCE = 2;
    private static final int MAX_THROW_DISTANCE = 4;
    
    public ThrowResult execute(Chef chef, char direction, int distance,
                               Tile[][] map, Chef otherChef) {
        
        // 1. Validasi Jarak
        if (distance < MIN_THROW_DISTANCE || distance > MAX_THROW_DISTANCE) {
            return new ThrowResult(false, null, null, 0, "Jarak lempar harus 2-4 kotak!");
        }
        
        // 2. Validasi Item
        Item heldItem = chef.getHeldItem(); 
        if (heldItem == null || !(heldItem instanceof Ingredient)) {
            return new ThrowResult(false, null, null, 0, "Hanya bisa melempar Ingredient!");
        }
        
        Ingredient ingredient = (Ingredient) heldItem;
        
        // 3. Validasi Masak
        if (ingredient.isCooked()) {
            return new ThrowResult(false, null, null, 0, "Tidak bisa melempar bahan matang!");
        }
        
        // 4. Hitung Arah
        int dx = 0, dy = 0;
        switch(direction) {
            case 'W': dy = -1; break;
            case 'S': dy = 1;  break;
            case 'A': dx = -1; break;
            case 'D': dx = 1;  break;
            default: return new ThrowResult(false, null, null, 0, "Arah tidak valid");
        }
        
        Position startPos = chef.getPosition();
        
        // 5. Kalkulasi Jalur
        ThrowPathResult pathResult = calculateThrowPath(startPos.getX(), startPos.getY(), dx, dy, distance, map);
        
        if (pathResult.landingPosition == null) {
            return new ThrowResult(false, null, null, 0, "Jalur terhalang tembok!");
        }
        
        Position landingPos = pathResult.landingPosition;
        
        // 6. Cek Catch (Tangkap)
        boolean caught = false;
        if (otherChef != null) {
            Position otherPos = otherChef.getPosition();
            if (otherPos.getX() == landingPos.getX() && otherPos.getY() == landingPos.getY()) {
                if (!otherChef.hasItem()) {
                    caught = true;
                    chef.setHeldItem(null);
                    otherChef.setHeldItem(ingredient);
                    return new ThrowResult(true, landingPos, ingredient,
                                             pathResult.actualDistance, 
                                             "Ditangkap oleh " + otherChef.getName(), true);
                } else {
                    caught = false; 
                }
            }
        }
        
        // 7. Jatuh ke Lantai
        if (!caught) {
            // Validasi akhir: Item tidak boleh mendarat DI ATAS station/counter
            // Item harus mendarat di lantai (Walkable)
            Tile landingTile = map[landingPos.getY()][landingPos.getX()];
            
            if (!landingTile.isWalkable()) {
                 return new ThrowResult(false, null, null, 0, "Item tidak bisa mendarat di atas station!");
            }

            chef.setHeldItem(null); 
            return new ThrowResult(true, landingPos, ingredient,
                                      pathResult.actualDistance, 
                                      "Jatuh di lantai (" + landingPos.getX() + "," + landingPos.getY() + ")", false);
        }
        
        return new ThrowResult(false, null, null, 0, "Error tidak diketahui");
    }
    
    private ThrowPathResult calculateThrowPath(int startX, int startY, int dx, int dy,
                                               int desiredDistance, Tile[][] map) {
        
        List<Position> path = new ArrayList<>();
        Position validLandingPos = null;
        int actualDistance = 0;
        
        for (int i = 1; i <= desiredDistance; i++) {
            int newX = startX + (dx * i);
            int newY = startY + (dy * i);
            Position testPos = new Position(newX, newY);
            
            
            // Cek apakah tile ini boleh DILEWATI (terbang di atasnya)?
            // Boleh lewat station (coklat), TIDAK boleh lewat tembok (merah)
            if (!isValidThrowPathTile(testPos, map)) {
                // Nabrak Tembok Merah -> Stop di sini
                // Posisi jatuh adalah tile sebelumnya (i-1)
                break; 
            }
            
            path.add(testPos);
            validLandingPos = testPos;
            actualDistance = i;
        }
        return new ThrowPathResult(validLandingPos, path, actualDistance);
    }
    
    /**
     * Cek apakah tile boleh dilewati oleh lemparan (Flying over).
     * Boleh: Lantai (Walkable), Station (Counter/Table).
     * Tidak Boleh: Tembok (Wall).
     */
    private boolean isValidThrowPathTile(Position pos, Tile[][] map) {
        int x = pos.getX();
        int y = pos.getY();
        
        // Cek bounds map
        if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) {
            return false;
        }
        
        Tile tile = map[y][x];
        
        // Lemparan hanya berhenti jika ketemu WALL.
        // Station/Counter/Table dianggap rendah, jadi bisa dilewati.
        if (tile.getType() == TileType.WALL) {
            return false; // Tembok Merah -> Blokir
        }
        return true;
    }

    public static class ThrowResult {
        public final boolean success;
        public final Position landingPosition;
        public final Ingredient ingredient;
        public final int actualDistance;
        public final String message;
        public final boolean caught;
        
        public ThrowResult(boolean success, Position landingPos, Ingredient ingredient,
                           int distance, String message) {
            this(success, landingPos, ingredient, distance, message, false);
        }
        
        public ThrowResult(boolean success, Position landingPos, Ingredient ingredient,
                           int distance, String message, boolean caught) {
            this.success = success;
            this.landingPosition = landingPos;
            this.ingredient = ingredient;
            this.actualDistance = distance;
            this.message = message;
            this.caught = caught;
        }
    }
    
    private static class ThrowPathResult {
        public final Position landingPosition;
        public final List<Position> path;
        public final int actualDistance;
        
        public ThrowPathResult(Position landingPos, List<Position> path, int distance) {
            this.landingPosition = landingPos;
            this.path = path;
            this.actualDistance = distance;
        }
    }
}