package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class PlayerSprite {

    public enum Direction {
        NORTH, SOUTH, EAST, WEST, 
        NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
    }

    // [direction][frameIndex] → BufferedImage
    private BufferedImage[][] frames = new BufferedImage[8][3];

    private Direction direction = Direction.SOUTH; // default hadap bawah
    private int frameIndex = 0;

    // animasi
    private long lastFrameTime = 0;
    private static final long FRAME_DURATION_NS = 150_000_000L; // 150 ms

    
    public PlayerSprite() {
        loadDirectionFrames(Direction.NORTH, "north");
        loadDirectionFrames(Direction.SOUTH, "south");
        loadDirectionFrames(Direction.EAST,  "east");
        loadDirectionFrames(Direction.WEST,  "west");
        loadDirectionFrames(Direction.NORTHEAST,  "northeast");
        loadDirectionFrames(Direction.NORTHWEST,  "northwest");
        loadDirectionFrames(Direction.SOUTHEAST,  "southeast");
        loadDirectionFrames(Direction.SOUTHWEST,  "southwest");
        System.out.println("=== Sprite Loading Complete ===\n");
    }

    private void loadDirectionFrames(Direction dir, String baseName) {
        int dirIndex = dirToIndex(dir);
        for (int i = 0; i < 3; i++) {
            frames[dirIndex][i] = loadImage(baseName + "_" + i + ".png");
        }
    }

    private BufferedImage loadImage(String filename) {
        // Path dari root project (GOSONG/)
        String path = "resources/player/" + filename;
        
        try {
            File file = new File(path);
            
            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return null;
            }
            
            BufferedImage img = ImageIO.read(file);
            System.out.println("yayyyyy Loaded: " + filename);
            return img;
            
        } catch (IOException e) {
            System.out.println("Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }

    private int dirToIndex(Direction dir) {
        return switch (dir) {
            case NORTH -> 0;
            case SOUTH -> 1;
            case EAST  -> 2;
            case WEST  -> 3;
            case NORTHEAST  -> 4;
            case NORTHWEST  -> 5;
            case SOUTHEAST  -> 6;
            case SOUTHWEST  -> 7;
        };
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    public Direction getDirection() {
        return direction;
    }

    /**
     * Dipanggil setiap frame dari GamePanel.update()
     * @param moving true kalau lagi gerak (tombol ditekan), false kalau idle.
     */
    public void updateAnimation(boolean moving) {
        long now = System.nanoTime();

        if (!moving) {
            // kalau diem, pakai frame idle (0)
            frameIndex = 0;
            lastFrameTime = now;
            return;
        }

        if (now - lastFrameTime > FRAME_DURATION_NS) {
            frameIndex = (frameIndex + 1) % 3; // 0 → 1 → 2 → 0 ...
            lastFrameTime = now;
        }
    }

    public void draw(Graphics2D g2, int x, int y, int size, boolean highlighted) {
        int dirIndex = dirToIndex(direction);
        BufferedImage img = frames[dirIndex][frameIndex];

        if (img != null) {
            g2.drawImage(img, x, y, size, size, null);
        } else {
            // Fallback kalau gambar gagal load
            g2.setColor(highlighted ? Color.CYAN : Color.RED);
            g2.fillRect(x, y, size, size);
            g2.setColor(Color.WHITE);
            g2.drawString("NO IMG", x + 5, y + size/2);
        }

        if (highlighted) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(x, y, size, size);
        }
    }
}




