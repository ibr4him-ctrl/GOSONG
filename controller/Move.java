package controller;

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
    
    // Hitung posisi baru berdasarkan direction
    private static Position calculateNewPosition(Position current, char direction) {
        int newX = current.getX();
        int newY = current.getY();
        
        switch(direction) {
            case 'W': // Atas
                newY--;
                break;
            case 'S': // Bawah
                newY++;
                break;
            case 'A': // Kiri
                newX--;
                break;
            case 'D': // Kanan
                newX++;
                break;
        }
        
        return new Position(newX, newY);
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
        
        // Cek apakah tile kosong dan tidak ada obstacle
        // Jika tile ada station, tidak bisa dilewati
        return !tile.isWalkable();
    }
}