
package actions;

import model.chef.Chef;
import model.map.tile.Tile;
import model.map.Position;

public class Move {
    // Method utama untuk movement
    public static void execute(Chef chef, char direction, Tile[][] map) {
        Position currentPos = chef.getPosition();
        Position newPos = calculateNewPosition(currentPos, direction);
        
        // Cek apakah posisi valid
        if (isValidPosition(newPos, map)) {
            chef.setPosition(newPos);
        }
    }
    
    // Hitung posisi baru berdasarkan direction menggunakan Position.add()
    private static Position calculateNewPosition(Position current, char direction) {
        switch(direction) {
            case 'W': // Atas
                return current.add(0, -1);
            case 'S': // Bawah
                return current.add(0, 1);
            case 'A': // Kiri
                return current.add(-1, 0);
            case 'D': // Kanan
                return current.add(1, 0);
            default:
                return current; // Tidak bergerak jika input tidak valid
        }
    }
    
    // Validasi posisi
    private static boolean isValidPosition(Position pos, Tile[][] map) {
        int x = pos.getX();
        int y = pos.getY();
        
        // Cek bounds
        if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) {
            return false;
        }
        
        Tile tile = map[y][x];
        
        // Cek apakah tile dapat dilalui (walkable)
        return tile.isWalkable();
    }
}

