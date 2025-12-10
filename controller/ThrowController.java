package controller;

import model.chef.Chef;
import model.item.Item;
import model.item.ingredient.Ingredient;
import model.map.Position;
import model.map.tile.Tile;

import java.util.ArrayList;
import java.util.List;

public class ThrowController {
    
    // Spesifikasi: "jarak lebih dari 1 kotak (bisa 2 atau 3 atau 4)"
    private static final int MIN_THROW_DISTANCE = 2;
    private static final int MAX_THROW_DISTANCE = 4;
    
    public ThrowResult execute(Chef chef, char direction, int distance,
                               Tile[][] map, Chef otherChef) {
        
        // 1. Validasi Jarak
        if (distance < MIN_THROW_DISTANCE || distance > MAX_THROW_DISTANCE) {
            return new ThrowResult(false, null, null, 0, "Jarak lempar harus 2-4 kotak!");
        }
        
        // 2. Validasi Item: Harus Ingredient
        Item heldItem = chef.getHeldItem(); 
        if (heldItem == null || !(heldItem instanceof Ingredient)) {
            return new ThrowResult(false, null, null, 0, "Hanya bisa melempar Ingredient!");
        }
        
        Ingredient ingredient = (Ingredient) heldItem;
        
        // 3. Validasi Spesifikasi: "Ingredients yang BELUM dimasak"
        if (ingredient.isCooked()) {
            return new ThrowResult(false, null, null, 0, "Tidak bisa melempar bahan matang!");
        }
        
        // 4. Hitung Arah (Delta X, Delta Y)
        int dx = 0, dy = 0;
        switch(direction) {
            case 'W': dy = -1; break; // Atas
            case 'S': dy = 1;  break; // Bawah
            case 'A': dx = -1; break; // Kiri
            case 'D': dx = 1;  break; // Kanan
            default: return new ThrowResult(false, null, null, 0, "Arah tidak valid");
        }
        
        Position startPos = chef.getPosition();
        
        // 5. Kalkulasi Jalur & Tembok (Sesuai Spesifikasi Foto)
        // Logika "Jatuh tepat sebelum tembok" ada di dalam method ini
        ThrowPathResult pathResult = calculateThrowPath(startPos.getX(), startPos.getY(), dx, dy, distance, map);
        
        // Jika pathResult.landingPosition null, artinya tepat di depan muka ada tembok (jarak 1 aja gabisa)
        if (pathResult.landingPosition == null) {
            return new ThrowResult(false, null, null, 0, "Jalur terhalang tembok!");
        }
        
        Position landingPos = pathResult.landingPosition;
        
        // 6. Cek Apakah Ditangkap Chef Lain?
        boolean caught = false;
        if (otherChef != null) {
            Position otherPos = otherChef.getPosition();
            
            // Jika posisi jatuh SAMA dengan posisi chef lain
            if (otherPos.getX() == landingPos.getX() && otherPos.getY() == landingPos.getY()) {
                
                // Syarat tangkap: Tangan penerima harus kosong
                if (!otherChef.hasItem()) {
                    // SUKSES DITANGKAP
                    caught = true;
                    chef.setHeldItem(null);          // Pengirim lepas
                    otherChef.setHeldItem(ingredient); // Penerima tangkap
                    
                    return new ThrowResult(true, landingPos, ingredient,
                                             pathResult.actualDistance, 
                                             "Ditangkap oleh " + otherChef.getName(), true);
                } else {
                    // Gagal tangkap karena tangan penuh -> Jatuh ke lantai
                    System.out.println("Gagal tangkap: Tangan " + otherChef.getName() + " penuh.");
                    caught = false; 
                }
            }
        }
        
        // 7. Jika Tidak Ditangkap (Jatuh ke Lantai)
        if (!caught) {
            chef.setHeldItem(null); // Item lepas dari tangan pengirim
            
            return new ThrowResult(true, landingPos, ingredient,
                                      pathResult.actualDistance, 
                                      "Jatuh di lantai (" + landingPos.getX() + "," + landingPos.getY() + ")", false);
        }
        
        return new ThrowResult(false, null, null, 0, "Error tidak diketahui");
    }
    
    /**
     * Method inti untuk logika "Jatuh sebelum tembok".
     */
    private ThrowPathResult calculateThrowPath(int startX, int startY, int dx, int dy,
                                               int desiredDistance, Tile[][] map) {
        
        List<Position> path = new ArrayList<>();
        Position validLandingPos = null;
        int actualDistance = 0;
        
        // Loop dari jarak 1 sampai jarak yang diinginkan
        for (int i = 1; i <= desiredDistance; i++) {
            int newX = startX + (dx * i);
            int newY = startY + (dy * i);
            Position testPos = new Position(newX, newY);
            
            // Cek apakah posisi ini Tembok / Tidak Valid?
            if (!isValidThrowPosition(testPos, map)) {
                // SPESIFIKASI: "Apabila lemparan melewati tembok, maka item jatuh tepat DI SEBELUM tembok"
                // Kita BREAK (berhenti) di sini. 
                // validLandingPos masih berisi posisi tile SEBELUMNYA (i-1).
                break; 
            }
            
            // Jika valid (bukan tembok), simpan posisi ini sebagai kandidat jatuh
            path.add(testPos);
            validLandingPos = testPos;
            actualDistance = i;
        }
        
        return new ThrowPathResult(validLandingPos, path, actualDistance);
    }
    
    // Cek apakah tile ada di dalam map dan Walkable (Lantai)
    private boolean isValidThrowPosition(Position pos, Tile[][] map) {
        int x = pos.getX();
        int y = pos.getY();
        
        if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) {
            return false;
        }
        
        return map[y][x].isWalkable(); // Return false jika Wall/Station
    }
    
    // --- Helper Classes (Result Wrapper) ---
    
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