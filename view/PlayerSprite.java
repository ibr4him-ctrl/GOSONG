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
    private final BufferedImage[][] frames = new BufferedImage[8][3];

    private Direction direction = Direction.SOUTH; // default hadap bawah
    private int frameIndex = 0;

    // animasi
    private long lastFrameTime = 0;
    private static final long FRAME_DURATION_NS = 150_000_000L; // 150 ms

    // ====== konfigurasi skin ======
    private final String folderName;   // "Chef1" / "Chef2"
    private final String variant;      // "blue" / "red"

    /**
     * Constructor utama:
     * @param folderName nama folder di bawah resources/player (misal "Chef1")
     * @param variant suffix warna di nama file (misal "blue" → east_0_blue.png)
     */
    public PlayerSprite(String folderName, String variant) {
        this.folderName = folderName;
        this.variant = variant;

        loadDirectionFrames(Direction.NORTH, "north");
        loadDirectionFrames(Direction.SOUTH, "south");
        loadDirectionFrames(Direction.EAST,  "east");
        loadDirectionFrames(Direction.WEST,  "west");
        loadDirectionFrames(Direction.NORTHEAST,  "northeast");
        loadDirectionFrames(Direction.NORTHWEST,  "northwest");
        loadDirectionFrames(Direction.SOUTHEAST,  "southeast");
        loadDirectionFrames(Direction.SOUTHWEST,  "southwest");

        System.out.println("=== Sprite Loading Complete: folder=" + folderName +
                           ", variant=" + variant + " ===");
    }

    // OPTIONAL: kalau masih mau constructor lama tanpa argumen
    public PlayerSprite() {
        this("Chef1", "blue"); // default bebas, bisa kamu ubah
    }

    private void loadDirectionFrames(Direction dir, String baseName) {
        int dirIndex = dirToIndex(dir);
        for (int i = 0; i < 3; i++) {
            // pola: east_0_blue.png / east_1_blue.png / ...
            String filename = baseName + "_" + i + "_" + variant + ".png";
            frames[dirIndex][i] = loadImage(filename);
        }
    }

    private BufferedImage loadImage(String filename) {
        // Path: resources/player/Chef1/east_0_blue.png
        String path = "resources/player/" + folderName + "/" + filename;

        try {
            File file = new File(path);

            if (!file.exists()) {
                System.out.println("[PlayerSprite] File not found: " + file.getAbsolutePath());
                return null;
            }

            BufferedImage img = ImageIO.read(file);
            System.out.println("[PlayerSprite] Loaded: " + path);
            return img;

        } catch (IOException e) {
            System.out.println("[PlayerSprite] Error loading " + path + ": " + e.getMessage());
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

    public void updateAnimation(boolean moving) {
        long now = System.nanoTime();

        if (!moving) {
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
            g2.setColor(highlighted ? Color.CYAN : Color.RED);
            g2.fillRect(x, y, size, size);
            g2.setColor(Color.WHITE);
            g2.drawString("NO IMG", x + 5, y + size / 2);
        }

        if (highlighted) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(x, y, size, size);
        }
    }
}
