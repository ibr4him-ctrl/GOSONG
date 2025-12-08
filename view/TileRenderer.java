package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import model.map.tile.TileType;

public class TileRenderer {

    private BufferedImage tileLight;
    private BufferedImage tileDark;

    public TileRenderer() {
        loadTiles();
    }

    private void loadTiles() {
        tileLight = loadImage("resources/tile/tile-light.png");
        tileDark  = loadImage("resources/tile/tile-dark.png");
    }

    private BufferedImage loadImage(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                System.out.println("[TileRenderer] File not found: " + f.getAbsolutePath());
                return null;
            }
            System.out.println("[TileRenderer] Loaded: " + f.getAbsolutePath());
            return ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("[TileRenderer] Error loading " + path + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Gambar 1 tile di (screenX, screenY) dengan ukuran tileSize.
     * xIndex,yIndex = indeks tile di map (0..WIDTH-1, 0..HEIGHT-1)
     */
    public void drawTile(Graphics2D g2,
                         TileType type,
                         int xIndex, int yIndex,
                         int screenX, int screenY,
                         int tileSize) {

        // =============== FLOOR CHECKERBOARD ===============
        // dipakai untuk tile WALKABLE (dan bisa ditambah SPAWN_CHEF kalau mau)
        if (type == TileType.WALKABLE) {
            BufferedImage tex =
                    ((xIndex + yIndex) % 2 == 0) ? tileLight : tileDark;

            if (tex != null) {
                g2.drawImage(tex, screenX, screenY, tileSize, tileSize, null);
            } else {
                // fallback kalau gambar gagal load
                Color c = ((xIndex + yIndex) % 2 == 0)
                        ? new Color(220, 220, 220)
                        : new Color(200, 200, 200);
                g2.setColor(c);
                g2.fillRect(screenX, screenY, tileSize, tileSize);
            }
        }
        // =============== TILE LAIN (STATION, WALL, DLL) ===============
        else {
            switch (type) {
                case WALL ->
                        g2.setColor(Color.DARK_GRAY);
                case ASSEMBLY_STATION ->
                        g2.setColor(new Color(139, 69, 19));
                case COOKING_STATION ->
                        g2.setColor(Color.RED);
                case SPAWN_CHEF ->
                        g2.setColor(Color.GREEN);
                case CUTTING_STATION ->
                        g2.setColor(Color.ORANGE);
                case PLATE_STORAGE ->
                        g2.setColor(Color.CYAN);
                case INGREDIENT_STORAGE ->
                        g2.setColor(Color.YELLOW);
                case SERVING_COUNTER ->
                        g2.setColor(Color.MAGENTA);
                case WASHING_STATION ->
                        g2.setColor(Color.BLUE);
                case TRASH ->
                        g2.setColor(new Color(128, 0, 0));
                default ->
                        g2.setColor(Color.WHITE);
            }
            g2.fillRect(screenX, screenY, tileSize, tileSize);
        }

        // // border tile hitam tipis
        // g2.setColor(Color.BLACK);
        // g2.drawRect(screenX, screenY, tileSize, tileSize);
    }
}
