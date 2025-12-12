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
import model.item.Item;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Dough;
import model.item.utensils.Plate;

public class AssemblyRenderer {

    /**
     * Key: "Dough_RAW", "Dough_COOKED", "Dough_BURNED", dst.
     * Juga dipakai untuk Plate: "Plate_CLEAN", "Plate_DIRTY".
     */
    private final Map<String, BufferedImage> ingredientSprites = new HashMap<>();

    public AssemblyRenderer() {
        loadAllSprites();
    }

    // =========================
    // LOAD SPRITE
    // =========================
    private void loadAllSprites() {
        // === PLATE ===
        loadSprite("Plate_CLEAN", "/resources/item/plate/PiringBersih.png");
        loadSprite("Plate_DIRTY", "/resources/item/plate/PiringKotor.png");

        // === DOUGH ===
        loadSprite("Dough_RAW",     "/resources/item/ingredients/Dough/DoughRaw.png");
        loadSprite("Dough_CHOPPED", "/resources/item/ingredients/Dough/DoughChopped.png");
        loadSprite("Dough_COOKED",  "/resources/item/ingredients/Dough/DoughCooked.png");
        loadSprite("Dough_BURNED",  "/resources/item/ingredients/Dough/DoughBurnt.png");

        // === TOMATO ===
        loadSprite("Tomato_RAW",     "/resources/item/ingredients/Tomato/TomatoRaw.png");
        loadSprite("Tomato_CHOPPED", "/resources/item/ingredients/Tomato/TomatoChopped.png");
        loadSprite("Tomato_COOKED",  "/resources/item/ingredients/Tomato/TomatoCooked.png");
        loadSprite("Tomato_BURNED",  "/resources/item/ingredients/Tomato/TomatoBurnt.png");

        // === CHEESE ===
        loadSprite("Cheese_RAW",     "/resources/item/ingredients/Cheese/CheeseRaw.png");
        loadSprite("Cheese_CHOPPED", "/resources/item/ingredients/Cheese/CheeseChopped.png");
        loadSprite("Cheese_COOKED",  "/resources/item/ingredients/Cheese/CheeseCooked.png");
        loadSprite("Cheese_BURNED",  "/resources/item/ingredients/Cheese/CheeseBurnt.png");

        // === CHICKEN ===
        loadSprite("Chicken_RAW",     "/resources/item/ingredients/Chicken/ChickenRaw.png");
        loadSprite("Chicken_CHOPPED", "/resources/item/ingredients/Chicken/ChickenChopped.png");
        loadSprite("Chicken_COOKED",  "/resources/item/ingredients/Chicken/ChickenCooked.png");
        loadSprite("Chicken_BURNED",  "/resources/item/ingredients/Chicken/ChickenBurnt.png");

        // === SAUSAGE ===
        loadSprite("Sausage_RAW",     "/resources/item/ingredients/Sausage/SausageRaw.png");
        loadSprite("Sausage_CHOPPED", "/resources/item/ingredients/Sausage/SausageChopped.png");
        loadSprite("Sausage_COOKED",  "/resources/item/ingredients/Sausage/SausageCooked.png");
        loadSprite("Sausage_BURNED",  "/resources/item/ingredients/Sausage/SausageBurnt.png");
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
        if (ing == null) return null;

        IngredientState state = ing.getState();
        String className = ing.getClass().getSimpleName(); // Dough / Tomato / ...

        String suffix;
        switch (state) {
            case RAW     -> suffix = "RAW";
            case CHOPPED -> suffix = "CHOPPED";
            case COOKED  -> suffix = "COOKED";
            case BURNED  -> suffix = "BURNED";
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

    public BufferedImage getSpriteForIngredient(Ingredient ing) {
        return getSprite(ing);
    }

    public BufferedImage getSpriteForPlate(Plate plate) {
        String key = (plate != null && plate.isClean()) ? "Plate_CLEAN" : "Plate_DIRTY";
        return ingredientSprites.get(key);
    }

    // =========================
    // HELPER: KUMPULKAN INGREDIENT dari Preparable
    // (Dish / Dough / Ingredient)
    // =========================
    private void collectIngredientsRecursive(Preparable prep, java.util.List<Ingredient> out) {
        if (prep == null) return;

        if (prep instanceof Ingredient ing) {
            out.add(ing);

            // kalau dia Dough, ikut tarik semua topping-nya
            if (ing instanceof Dough dough) {
                if (dough.getToppings() != null) {
                    for (Ingredient t : dough.getToppings()) {
                        collectIngredientsRecursive(t, out);
                    }
                }
            }

        } else if (prep instanceof Dish dish) {
            if (dish.getComponents() != null) {
                for (Preparable sub : dish.getComponents()) {
                    collectIngredientsRecursive(sub, out);
                }
            }
        }
    }

    // =========================
    // RENDER PLATE DI ASSEMBLY STATION
    // =========================
    public void drawPlateOnAssembly(Graphics2D g2,
                                    int tileX, int tileY,
                                    int tileSize,
                                    Plate plate) {
        if (plate == null) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = tileX + tileSize / 2;
        int centerY = tileY + tileSize / 2;

        // --- 1. Gambar piring pakai sprite ---
        BufferedImage plateSprite = getSpriteForPlate(plate);
        int plateSize = (int) (tileSize * 0.9);
        int plateX = centerX - plateSize / 2;
        int plateY = centerY - plateSize / 2;

        if (plateSprite != null) {
            g2.drawImage(plateSprite, plateX, plateY, plateSize, plateSize, null);
        } else {
            // fallback: kalau sprite nggak ketemu, pakai lingkaran lama
            int plateRadius = (int) (tileSize * 0.40);
            g2.setColor(new Color(245, 245, 250));
            g2.fillOval(centerX - plateRadius, centerY - plateRadius,
                        plateRadius * 2, plateRadius * 2);
            g2.setColor(new Color(200, 200, 210));
            g2.drawOval(centerX - plateRadius, centerY - plateRadius,
                        plateRadius * 2, plateRadius * 2);
        }

        // Kalau piring kotor → cukup pakai sprite piring kotor, tidak render isi
        if (!plate.isClean()) {
            return;
        }

        // --- 2. CEK APAKAH ADA PIZZA MATANG (finalDish) ---
        Dish finalDish = plate.getDish();
        if (finalDish != null) {
            // Render pizza matang sebagai dish
            renderDishOnPlate(g2, centerX, centerY, tileSize, finalDish);
            
            // Info kecil jumlah isi plate (untuk dish, tampilkan "1")
            g2.setColor(Color.BLACK);
            g2.drawString("x1", tileX + tileSize - 16, tileY + tileSize - 4);
            return;
        }

        // --- 3. Kumpulkan SEMUA Ingredient dari plate (langsung & nested) ---
        java.util.List<Ingredient> allIngredients = new java.util.ArrayList<>();
        java.util.Set<Preparable> contents = plate.getContents();
        if (contents != null) {
            for (Preparable prep : contents) {
                collectIngredientsRecursive(prep, allIngredients);
            }
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

        // --- 3. Gambar dough base ---
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

        // --- 4. Gambar topping ditumpuk di atas dough ---
        if (!toppings.isEmpty()) {
            int toppingSize = (int) (tileSize * 0.40);
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
        int count = (contents == null) ? 0 : contents.size();
        g2.drawString("x" + count,
                      tileX + tileSize - 16,
                      tileY + tileSize - 4);
    }

    public void drawPlateOnAssembly(Graphics2D g2,
                                    int tileX, int tileY,
                                    int tileSize,
                                    Plate plate,
                                    boolean drawCountLabel) {
        if (plate == null) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = tileX + tileSize / 2;
        int centerY = tileY + tileSize / 2;

        // --- 1. Gambar piring pakai sprite ---
        BufferedImage plateSprite = getSpriteForPlate(plate);
        int plateSize = (int) (tileSize * 0.9);
        int plateX = centerX - plateSize / 2;
        int plateY = centerY - plateSize / 2;

        if (plateSprite != null) {
            g2.drawImage(plateSprite, plateX, plateY, plateSize, plateSize, null);
        } else {
            int plateRadius = (int) (tileSize * 0.40);
            g2.setColor(new Color(245, 245, 250));
            g2.fillOval(centerX - plateRadius, centerY - plateRadius,
                    plateRadius * 2, plateRadius * 2);
            g2.setColor(new Color(200, 200, 210));
            g2.drawOval(centerX - plateRadius, centerY - plateRadius,
                    plateRadius * 2, plateRadius * 2);
        }

        // Kalau piring kotor → cukup pakai sprite piring kotor, tidak render isi
        if (!plate.isClean()) {
            return;
        }

        // --- 2. CEK APAKAH ADA PIZZA MATANG (finalDish) ---
        Dish finalDish = plate.getDish();
        if (finalDish != null) {
            // Render pizza matang sebagai dish
            renderDishOnPlate(g2, centerX, centerY, tileSize, finalDish);
            
            // Info kecil jumlah isi plate (untuk dish, tampilkan "1")
            g2.setColor(Color.BLACK);
            g2.drawString("x1", tileX + tileSize - 16, tileY + tileSize - 4);
            return;
        }

        // --- 3. Kumpulkan SEMUA Ingredient dari plate (langsung & nested) ---
        java.util.List<Ingredient> allIngredients = new java.util.ArrayList<>();
        java.util.Set<Preparable> contents = plate.getContents();
        if (contents != null) {
            for (Preparable prep : contents) {
                collectIngredientsRecursive(prep, allIngredients);
            }
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

        // --- 3. Gambar dough base ---
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

        // --- 4. Gambar topping ditumpuk di atas dough ---
        if (!toppings.isEmpty()) {
            int toppingSize = (int) (tileSize * 0.40);
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

        // --- 5. Label kecil jumlah isi plate (xN) ---
        if (drawCountLabel) {
            g2.setColor(Color.BLACK);
            int count = (contents == null) ? 0 : contents.size();
            g2.drawString("x" + count,
                    tileX + tileSize - 16,
                    tileY + tileSize - 4);
        }
    }


    // =========================
    // RENDER DOUGH LANGSUNG DI ASSEMBLY (TANPA PLATE)
    // =========================
    public void drawDoughOnAssembly(Graphics2D g2,
                                    int tileX, int tileY,
                                    int tileSize,
                                    Dough dough) {
        if (dough == null) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = tileX + tileSize / 2;
        int centerY = tileY + tileSize / 2;

        // 1) gambar dough base sesuai state
        BufferedImage doughSprite = getSprite(dough);
        int size = (int) (tileSize * 0.7);
        int x = centerX - size / 2;
        int y = centerY - size / 2;

        if (doughSprite != null) {
            g2.drawImage(doughSprite, x, y, size, size, null);
        } else {
            g2.setColor(new Color(210, 180, 120));
            g2.fillOval(x, y, size, size);
        }

        // 2) gambar topping yang nempel di dough
        if (dough.getToppings() != null && !dough.getToppings().isEmpty()) {
            int toppingSize = (int) (tileSize * 0.4);
            int drawX = centerX - toppingSize / 2;
            int drawY = centerY - toppingSize / 2;

            for (Ingredient topping : dough.getToppings()) {
                BufferedImage sprite = getSprite(topping);

                if (sprite != null) {
                    g2.drawImage(sprite, drawX, drawY, toppingSize, toppingSize, null);
                } else {
                    g2.setColor(Color.RED);
                    g2.fillOval(drawX, drawY, toppingSize, toppingSize);
                }
            }
        }
    }

    public BufferedImage renderIconForItem(Item item, int size) {
        if (item == null || size <= 0) return null;

        BufferedImage canvas = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = canvas.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // transparan background
        g2.setComposite(java.awt.AlphaComposite.SrcOver);

        if (item instanceof Plate plate) {
            // render plate + isi (tanpa tulisan "xN")
            drawPlateOnAssembly(g2, 0, 0, size, plate, false);
        }
        else if (item instanceof Dough dough) {
            drawDoughOnAssembly(g2, 0, 0, size, dough);
        }
        else if (item instanceof Ingredient ing) {
            BufferedImage sp = getSpriteForIngredient(ing);
            if (sp != null) {
                int pad = (int)(size * 0.1);
                g2.drawImage(sp, pad, pad, size - pad*2, size - pad*2, null);
            }
        }
        else if (item instanceof Dish dish) {
            // coba render isi dish (ambil dough+topping kalau ada)
            java.util.List<Ingredient> all = new java.util.ArrayList<>();
            collectIngredientsRecursive(dish, all);

            Dough base = null;
            java.util.List<Ingredient> tops = new java.util.ArrayList<>();
            for (Ingredient ing : all) {
                if (ing instanceof Dough d) base = d;
                else tops.add(ing);
            }

            if (base != null) {
                drawDoughOnAssembly(g2, 0, 0, size, base);
            }
        }

        g2.dispose();
        return canvas;
    }

    /**
     * Render pizza matang (Dish) di atas plate
     */
    private void renderDishOnPlate(Graphics2D g2, int centerX, int centerY, int tileSize, Dish dish) {
        // Kumpulkan semua ingredient dari dish
        java.util.List<Ingredient> allIngredients = new java.util.ArrayList<>();
        collectIngredientsRecursive(dish, allIngredients);
        
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
        
        // Gambar dough base
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
        
        // Gambar topping ditumpuk di atas dough
        if (!toppings.isEmpty()) {
            int toppingSize = (int) (tileSize * 0.40);
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
    }
}