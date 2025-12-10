package controller;

import model.chef.Chef;
import model.map.Position;
import model.map.tile.Tile;

public class DashController {
    
    private static final int DASH_DISTANCE = 3; 
    private static final long DASH_COOLDOWN_NS = 3_000_000_000L; 

    // Cooldown tracking per chef
    private long lastDashTimeChef1 = 0L;
    private long lastDashTimeChef2 = 0L;
    
    // Edge detection per chef (FIX: separated state)
    private boolean wasShiftPressedChef1 = false;
    private boolean wasShiftPressedChef2 = false;
    
    /**
     * Execute dash untuk chef yang sedang aktif
     * 
     * @param chef Chef yang akan dash
     * @param direction Arah dash (W/A/S/D)
     * @param map Tile map untuk collision detection
     * @param isFirstChef Apakah ini chef 1 atau chef 2 (untuk tracking cooldown)
     * @param shiftPressed Apakah tombol Shift sedang ditekan
     * @return DashResult dengan informasi hasil dash
     */
    public DashResult execute(Chef chef, char direction, Tile[][] map, 
                             boolean isFirstChef, boolean shiftPressed) {
        
        long now = System.nanoTime();
        long lastDashTime = isFirstChef ? lastDashTimeChef1 : lastDashTimeChef2;
        boolean wasPressed = isFirstChef ? wasShiftPressedChef1 : wasShiftPressedChef2;
        
        // Reset flag saat Shift dilepas
        if (!shiftPressed) {
            if (isFirstChef) {
                wasShiftPressedChef1 = false;
            } else {
                wasShiftPressedChef2 = false;
            }
            return new DashResult(false, 0, "Shift not pressed");
        }
        
        // Deteksi edge: hanya trigger saat Shift BARU ditekan
        if (wasPressed) {
            return new DashResult(false, 0, "Shift still held");
        }
        
        // Set flag bahwa Shift sudah ditekan untuk chef ini
        if (isFirstChef) {
            wasShiftPressedChef1 = true;
        } else {
            wasShiftPressedChef2 = true;
        }
        
        // Check cooldown
        if (now - lastDashTime < DASH_COOLDOWN_NS) {
            double remainingSeconds = (DASH_COOLDOWN_NS - (now - lastDashTime)) / 1_000_000_000.0;
            String message = String.format("[Dash] Cooldown: %.1fs remaining", remainingSeconds);
            System.out.println(message);
            return new DashResult(false, 0, message);
        }
        
        // Calculate dash direction
        int dx = 0, dy = 0;
        switch(direction) {
            case 'W': dy = -1; break; // Atas
            case 'S': dy = 1;  break; // Bawah
            case 'A': dx = -1; break; // Kiri
            case 'D': dx = 1;  break; // Kanan
            default: return new DashResult(false, 0, "Invalid direction");
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
            String message = "[Dash] Blocked! Cannot dash in that direction.";
            System.out.println(message);
            return new DashResult(false, 0, message);
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
        
        String message = String.format("[Dash] %s dashed %d tiles!", 
                                      chef.getName(), tilesMovedCount);
        System.out.println(message);
        
        return new DashResult(true, tilesMovedCount, message);
    }
    
    public boolean executeSimple(Chef chef, char direction, Tile[][] map, 
                                 boolean isFirstChef, boolean shiftPressed) {
        DashResult result = execute(chef, direction, map, isFirstChef, shiftPressed);
        return result.success;
    }
    
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
    
    public double getRemainingCooldown(boolean isFirstChef) {
        long now = System.nanoTime();
        long lastDashTime = isFirstChef ? lastDashTimeChef1 : lastDashTimeChef2;
        
        long elapsed = now - lastDashTime;
        if (elapsed >= DASH_COOLDOWN_NS) {
            return 0.0;
        }
        
        return (DASH_COOLDOWN_NS - elapsed) / 1_000_000_000.0;
    }
    
    public String getRemainingCooldownString(boolean isFirstChef) {
        double remaining = getRemainingCooldown(isFirstChef);
        if (remaining <= 0.0) {
            return "Ready!";
        }
        return String.format("%.1fs", remaining);
    }
    
    public boolean canDash(boolean isFirstChef) {
        return getRemainingCooldown(isFirstChef) <= 0.0;
    }
    
    public void resetCooldown(boolean isFirstChef) {
        if (isFirstChef) {
            lastDashTimeChef1 = 0L;
        } else {
            lastDashTimeChef2 = 0L;
        }
    }
    
    public void reset() {
        lastDashTimeChef1 = 0L;
        lastDashTimeChef2 = 0L;
        wasShiftPressedChef1 = false;
        wasShiftPressedChef2 = false;
    }
    
    public int getDashDistance() {
        return DASH_DISTANCE;
    }
    
    public double getCooldownDuration() {
        return DASH_COOLDOWN_NS / 1_000_000_000.0;
    }
    
    public static class DashResult {
        public final boolean success;
        public final int tilesMovedCount;
        public final String message;
        
        public DashResult(boolean success, int tiles, String message) {
            this.success = success;
            this.tilesMovedCount = tiles;
            this.message = message;
        }
        
        @Override
        public String toString() {
            return String.format("DashResult{success=%s, tiles=%d, message='%s'}", 
                               success, tilesMovedCount, message);
        }
    }
}