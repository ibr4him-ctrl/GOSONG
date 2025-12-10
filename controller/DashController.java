package controller;

import model.chef.Chef;
import model.map.Position;
import model.map.tile.Tile;

/**
 * DashController - Menghandle dash movement untuk chef
 * 
 * Fitur:
 * - Dash bergerak 3 tile ke arah yang dituju
 * - Cooldown 3 detik antar dash
 * - Dash akan berhenti jika bertemu tile yang tidak walkable
 */
public class DashController {
    
    private static final int DASH_DISTANCE = 3; // Bergerak 3 tile
    private static final long DASH_COOLDOWN_NS = 3_000_000_000L; // 3 detik dalam nanoseconds
    
    private long lastDashTimeChef1 = 0L;
    private long lastDashTimeChef2 = 0L;
    
    private boolean wasShiftPressed = false;
    
    /**
     * Execute dash untuk chef yang sedang aktif
     * 
     * @param chef Chef yang akan dash
     * @param direction Arah dash (W/A/S/D)
     * @param map Tile map untuk collision detection
     * @param isFirstChef Apakah ini chef 1 atau chef 2 (untuk tracking cooldown)
     * @param shiftPressed Apakah tombol Shift sedang ditekan
     * @return true jika dash berhasil, false jika masih cooldown atau invalid
     */
    public boolean execute(Chef chef, char direction, Tile[][] map, 
                          boolean isFirstChef, boolean shiftPressed) {
        
        long now = System.nanoTime();
        long lastDashTime = isFirstChef ? lastDashTimeChef1 : lastDashTimeChef2;
        
        // Deteksi edge: dash hanya trigger saat Shift baru ditekan (bukan hold)
        if (!shiftPressed) {
            wasShiftPressed = false;
            return false;
        }
        
        if (wasShiftPressed) {
            return false; // Masih hold, jangan dash lagi
        }
        
        wasShiftPressed = true;
        
        // Check cooldown
        if (now - lastDashTime < DASH_COOLDOWN_NS) {
            double remainingSeconds = (DASH_COOLDOWN_NS - (now - lastDashTime)) / 1_000_000_000.0;
            System.out.println("[Dash] Cooldown: " + String.format("%.1f", remainingSeconds) + "s remaining");
            return false;
        }
        
        // Calculate dash direction
        int dx = 0, dy = 0;
        switch(direction) {
            case 'W': dy = -1; break; // Atas
            case 'S': dy = 1;  break; // Bawah
            case 'A': dx = -1; break; // Kiri
            case 'D': dx = 1;  break; // Kanan
            default: return false;
        }
        
        Position currentPos = chef.getPosition();
        int startX = currentPos.getX();
        int startY = currentPos.getY();
        
        // Cek setiap tile dalam dash path
        int tilesMovedCount = 0;
        for (int i = 1; i <= DASH_DISTANCE; i++) {
            int newX = startX + (dx * i);
            int newY = startY + (dy * i);
            
            Position testPos = new Position(newX, newY);
            
            // Stop jika bertemu tile yang tidak walkable atau keluar map
            if (!isValidPosition(testPos, map)) {
                break;
            }
            
            tilesMovedCount = i;
        }
        
        // Jika tidak bisa gerak sama sekali, dash gagal
        if (tilesMovedCount == 0) {
            System.out.println("[Dash] Blocked! Cannot dash in that direction.");
            return false;
        }
        
        // Execute dash
        int finalX = startX + (dx * tilesMovedCount);
        int finalY = startY + (dy * tilesMovedCount);
        chef.setPosition(finalX, finalY);
        
        // Update cooldown
        if (isFirstChef) {
            lastDashTimeChef1 = now;
        } else {
            lastDashTimeChef2 = now;
        }
        
        System.out.println("[Dash] " + chef.getName() + " dashed " + 
                          tilesMovedCount + " tiles!");
        
        return true;
    }
    
    /**
     * Validasi apakah posisi bisa dilalui
     */
    private boolean isValidPosition(Position pos, Tile[][] map) {
        int x = pos.getX();
        int y = pos.getY();
        
        // Cek bounds
        if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) {
            return false;
        }
        
        Tile tile = map[y][x];
        return tile.isWalkable();
    }
    
    /**
     * Get remaining cooldown untuk chef tertentu
     * @return Sisa cooldown dalam detik, atau 0 jika sudah bisa dash
     */
    public double getRemainingCooldown(boolean isFirstChef) {
        long now = System.nanoTime();
        long lastDashTime = isFirstChef ? lastDashTimeChef1 : lastDashTimeChef2;
        
        long elapsed = now - lastDashTime;
        if (elapsed >= DASH_COOLDOWN_NS) {
            return 0.0;
        }
        
        return (DASH_COOLDOWN_NS - elapsed) / 1_000_000_000.0;
    }
    
    /**
     * Check apakah chef bisa dash (tidak dalam cooldown)
     */
    public boolean canDash(boolean isFirstChef) {
        return getRemainingCooldown(isFirstChef) <= 0.0;
    }
    
    /**
     * Reset dash state (untuk testing atau restart)
     */
    public void reset() {
        lastDashTimeChef1 = 0L;
        lastDashTimeChef2 = 0L;
        wasShiftPressed = false;
    }
}