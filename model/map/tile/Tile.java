package model.map.tile;

import model.map.Position;
import model.item.Item;

public class Tile {

    private final Position position;
    private final TileType type;
    private Item item;

    public Tile(Position position, TileType type) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("TileType cannot be null.");
        }

        this.position = position;
        this.type = type;
        this.item = null;
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
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
