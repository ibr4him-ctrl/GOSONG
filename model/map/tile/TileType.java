package model.map.tile;  

public enum TileType {
    CUTTING_STATION('C', false), 
    COOKING_STATION('R', false), 
    ASSEMBLY_STATION('A',false),
    SERVING_COUNTER('S',false), 
    WASHING_STATION('W',false), 
    INGREDIENT_STORAGE('I',false), 
    PLATE_STORAGE('P',false), 
    TRASH('T',false), 
    WALL('X',false), 
    WALKABLE('.',true), 
    SPAWN_CHEF('V',true); 

    private final char symbol; 
    private final boolean walkable; 

    TileType(char symbol, boolean walkable){
        this.symbol = symbol; 
        this.walkable = walkable; 
    }

    public char getSymbol(){
        return symbol; 
    }

    public boolean isWalkable(){
        return walkable; 
    }
    public static TileType fromSymbol(char c) {
        for (TileType type : values()) {
            if (type.symbol == c) {
                return type;
            }
        }
        throw new IllegalArgumentException("Simbol tile tidak dikenal: " + c);
    }
}