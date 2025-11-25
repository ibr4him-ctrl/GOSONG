package model.map.tile;

import model.map.Position;

public class Tile {

    private final Position position;
    private final TileType type;

    public Tile(Position position, TileType type) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("TileType cannot be null.");
        }

        this.position = position;
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() {
        return type.isWalkable();
    }

    public char getSymbol() {
        return type.getSymbol();
    }

    @Override
    public String toString() {
        return "Tile{" + "position=" + position + ", type=" + type +'}';
    }
}
