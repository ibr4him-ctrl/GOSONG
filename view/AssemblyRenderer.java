package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import model.enums.IngredientState;
import model.item.Dish;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Dough;
import model.item.utensils.Plate;

public class AssemblyRenderer {

    /**
     * Key: "Dough_RAW", "Dough_COOKED", "Dough_BURNT", dst.
     */
    private final Map<String, BufferedImage> ingredientSprites = new HashMap<>();

    public AssemblyRenderer() {
        loadAllSprites();
    }

    // =========================
    // LOAD SPRITE
    // =========================
    private void loadAllSprites() {
        // NOTE:
        // - Key-nya RAW / CHOPPED / COOKED / BURNT
        // - Enum state = RAW / CHOPPED / COOKED / BURNED
        //   → nanti di getSprite, BURNED akan dipetakan ke BURNT

        // === DOUGH ===
        loadSprite("Dough_RAW",     "/resources/item/ingredients/Dough/DoughRaw.png");
        loadSprite("Dough_CHOPPED", "/resources/item/ingredients/Dough/DoughChopped.png");
        loadSprite("Dough_COOKED",  "/resources/item/ingredients/Dough/DoughCooked.png");
        loadSprite("Dough_BURNED",   "/resources/item/ingredients/Dough/DoughBurnt.png");

        // === TOMATO ===
        loadSprite("Tomato_RAW",     "/resources/item/ingredients/Tomato/TomatoRaw.png");
        loadSprite("Tomato_CHOPPED", "/resources/item/ingredients/Tomato/TomatoChopped.png");
        loadSprite("Tomato_COOKED",  "/resources/item/ingredients/Tomato/TomatoCooked.png");
        loadSprite("Tomato_BURNED",   "/resources/item/ingredients/Tomato/TomatoBurnt.png");

        // === CHEESE ===
        loadSprite("Cheese_RAW",     "/resources/item/ingredients/Cheese/CheeseRaw.png");
        loadSprite("Cheese_CHOPPED", "/resources/item/ingredients/Cheese/CheeseChopped.png");
        loadSprite("Cheese_COOKED",  "/resources/item/ingredients/Cheese/CheeseCooked.png");
        loadSprite("Cheese_BURNED",   "/resources/item/ingredients/Cheese/CheeseBurnt.png");

        // === CHICKEN ===
        loadSprite("Chicken_RAW",     "/resources/item/ingredients/Chicken/ChickenRaw.png");
        loadSprite("Chicken_CHOPPED", "/resources/item/ingredients/Chicken/ChickenChopped.png");
        loadSprite("Chicken_COOKED",  "/resources/item/ingredients/Chicken/ChickenCooked.png");
        loadSprite("Chicken_BURNED",   "/resources/item/ingredients/Chicken/ChickenBurnt.png");

        // === SAUSAGE ===
        loadSprite("Sausage_RAW",     "/resources/item/ingredients/Sausage/SausageRaw.png");
        loadSprite("Sausage_CHOPPED", "/resources/item/ingredients/Sausage/SausageChopped.png");
        loadSprite("Sausage_COOKED",  "/resources/item/ingredients/Sausage/SausageCooked.png");
        loadSprite("Sausage_BURNED",  "/resources/item/ingredients/Sausage/SausageBurnt.png");

        // Kalau di project-mu foldernya "Daugh", ganti path di atas sesuai nama folder/filenya.
    }

    private void loadSprite(String key, String path) {
        try {
            var is = getClass().getResourceAsStream(path);
            if (is == null) {
                System.err.println("[AssemblyRenderer] Sprite not found: " + path);
                return;
            }
            BufferedImage img = ImageIO.read(is);
            ingredientSprites.put(key, img);
        } catch (IOException e) {
            System.err.println("[AssemblyRenderer] Failed to load: " + path);
            e.printStackTrace();
        }
    }

    // =========================
    // HELPER: GET SPRITE BY STATE
    // =========================
    private BufferedImage getSprite(Ingredient ing) {
        IngredientState state = ing.getState();
        String className = ing.getClass().getSimpleName(); // Dough / Tomato / ...

        String suffix;
        switch (state) {
            case RAW     -> suffix = "RAW";
            case CHOPPED -> suffix = "CHOPPED";
            case COOKED  -> suffix = "COOKED";
            case BURNED  -> suffix = "BURNT";   // enum BURNED → file BURNT
            default      -> suffix = "RAW";
        }

        String key = className + "_" + suffix;

        BufferedImage img = ingredientSprites.get(key);
        if (img == null) {
            // fallback ke RAW kalau sprite state itu belum ada
            img = ingredientSprites.get(className + "_RAW");
        }
        return img;
    }

    // =========================
    // HELPER: KUMPULKAN INGREDIENT DARI Preparable (Dish / Ingredient)
    // =========================
    private void collectIngredientsRecursive(Preparable prep, java.util.List<Ingredient> out) {
        if (prep instanceof Ingredient ing) {
            out.add(ing);
        } else if (prep instanceof Dish dish) {
            for (Preparable sub : dish.getComponents()) {
                collectIngredientsRecursive(sub, out);
            }
        }
    }

    // =========================
    // RENDER PLATE DI ASSEMBLY STATION
    // =========================
    public void drawPlateOnAssembly(Graphics2D g2, int tileX, int tileY, int tileSize, Plate plate) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = tileX + tileSize / 2;
        int centerY = tileY + tileSize / 2;

        // --- 1. Gambar piring ---
        int plateRadius = (int) (tileSize * 0.40);
        g2.setColor(new Color(245, 245, 250));
        g2.fillOval(centerX - plateRadius, centerY - plateRadius,
                    plateRadius * 2, plateRadius * 2);
        g2.setColor(new Color(200, 200, 210));
        g2.drawOval(centerX - plateRadius, centerY - plateRadius,
                    plateRadius * 2, plateRadius * 2);

        if (!plate.isClean()) {
            g2.setColor(new Color(80, 80, 80, 120));
            g2.fillOval(centerX - plateRadius, centerY - plateRadius,
                        plateRadius * 2, plateRadius * 2);
            g2.setColor(Color.BLACK);
            g2.drawString("Dirty", tileX + 4, tileY + tileSize - 4);
            return;
        }

        // --- 2. Kumpulkan SEMUA Ingredient dari plate (langsung & dari dalam Dish) ---
        java.util.List<Ingredient> allIngredients = new java.util.ArrayList<>();
        for (Preparable prep : plate.getContents()) {
            collectIngredientsRecursive(prep, allIngredients);
        }

        // Pisahkan dough & topping
        Ingredient doughBase = null;
        java.util.List<Ingredient> toppings = new java.util.ArrayList<>();

        for (Ingredient ing : allIngredients) {
            if (ing instanceof Dough) {
                doughBase = ing;
            } else {
                toppings.add(ing);
            }
        }

        // --- 3. Gambar dough base (bisa RAW / COOKED / BURNED, sesuai state) ---
        if (doughBase != null) {
            BufferedImage doughSprite = getSprite(doughBase);
            int size = (int) (tileSize * 0.70);
            int x = centerX - size / 2;
            int y = centerY - size / 2;

            if (doughSprite != null) {
                g2.drawImage(doughSprite, x, y, size, size, null);
            } else {
                g2.setColor(new Color(210, 180, 120));
                g2.fillOval(x, y, size, size);
            }
        }

        // --- 4. Gambar topping DITIMPA di atas dough (semua di tengah) ---
        if (!toppings.isEmpty()) {
            int toppingSize = (int) (tileSize * 0.40);

            // posisi tengah sekali
            int drawX = centerX - toppingSize / 2;
            int drawY = centerY - toppingSize / 2;

            for (Ingredient ing : toppings) {
                BufferedImage sprite = getSprite(ing);

                if (sprite != null) {
                    g2.drawImage(sprite, drawX, drawY, toppingSize, toppingSize, null);
                } else {
                    g2.setColor(Color.RED);
                    g2.fillOval(drawX, drawY, toppingSize, toppingSize);
                }
            }
        }

        // --- 5. Info kecil jumlah isi plate ---
        g2.setColor(Color.BLACK);
        g2.drawString("x" + plate.getContents().size(),
                      tileX + tileSize - 16,
                      tileY + tileSize - 4);
    }
}
