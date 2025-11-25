package model.map;

import java.util.ArrayList;
import java.util.List;
import model.map.tile.Tile;
import model.map.tile.TileType;

public class PizzaMap {
    public static final int WIDTH = 14;  //kolom 
    public static final int HEIGHT = 10;  //baris 

    private final Tile[][] tiles; 
    private final List<Position> spawnpoints = new ArrayList<>(); 

    private static final char[][] LAYOUT = {
            {'X','A','T','A','C','A','A','A','C','A','A','A','X','X'},
            {'X','.','.','.','.','.','.','.','.','.','.','.','X','X'},
            {'X','.','.','.','.','.','A','.','V','.','.','.','S','X'},
            {'X','.','.','.','.','.','.','.','.','.','.','.','S','X'},
            {'X','W','W','A','I','A','I','A','I','A','I','A','P','X'},
            {'X','.','.','.','.','.','.','.','.','.','.','.','.','X'},
            {'X','X','X','X','.','.','A','.','.','.','X','X','X','X'},
            {'X','R','.','.','.','V','.','.','.','.','.','.','R','X'},
            {'X','X','X','X','.','.','.','.','.','.','X','X','X','X'},
            {'X','X','X','X','A','A','I','A','A','A','X','X','X','X'}
    };

    public PizzaMap(){
        tiles = new Tile[HEIGHT][WIDTH]; 

        for (int row = 0; row < HEIGHT; row++){
            for (int col = 0; col < WIDTH; col++){
                char symbol = LAYOUT[row][col]; 
                TileType type = TileType.fromSymbol(symbol); 

                Position pos = new Position(col,row); 
                Tile tile = new Tile(pos,type); 

                tiles[row][col] = tile; 
                if (type == TileType.SPAWN_CHEF){
                    spawnpoints.add(pos); 
                }
            }
        }
    }
    public Tile getTileAt(int x, int y){
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT){
            throw new IndexOutOfBoundsException("Coord out of map: (" + x + "," + y + ")");
        }
        return tiles[y][x]; 
    }
    public boolean isWalkable(int x, int y) {
        return getTileAt(x, y).isWalkable();
    }

    public List<Position> getSpawnPoints() {
        return new ArrayList<>(spawnpoints);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    /**
     * Untuk debug: print map ke console sesuai simbol aslinya.
     */
    public void printToConsole() {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                System.out.print(tiles[row][col].getSymbol());
            }
            System.out.println();
        }
    }
}