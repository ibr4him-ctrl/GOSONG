package model.item.utensils;

import model.item.Item;
import model.item.ingredient.pizza.*;
import model.item.dish.pizzaMargherita;
import model.item.dish.pizzaAyam;
import model.item.dish.pizzaSosis;
import model.item.Dish;

public class oven extends Item {
    private DoughFinalMixed dough; // Adonan yang sedang dimasak
    private int cookingTime; // Waktu memasak tersisa
    private boolean isCooking; // Status sedang memasak
    private static final int DEFAULT_COOKING_TIME = 5; // Waktu standar memasak
    private boolean isBurned; // Status apakah makanan gosong
    private Dish cookedDish; // Hasil masakan yang sudah matang

    public oven() {
        super("Oven", Item.ItemType.UTENSIL, Item.ItemLocation.COUNTER);
        this.dough = null;
        this.cookingTime = 0;
        this.isCooking = false;
    }

    // Memasukkan adonan ke dalam oven
    public boolean insertDough(DoughFinalMixed dough) {
        if (dough == null || dough.getState() != DoughFinalMixed.DoughState.READY_TO_BAKE) {
            return false; // Bukan adonan yang valid atau belum siap dipanggang
        }
        if (this.dough == null && !isCooking) {
            this.dough = dough;
            return true;
        }
        return false;
    }

    // Memulai proses pemanggangan
    public boolean startCooking() {
        if (dough != null && !isCooking) {
            this.cookingTime = DEFAULT_COOKING_TIME;
            this.isCooking = true;
            return true;
        }
        return false;
    }

    // Update status pemanggangan
    public void update() {
        if (isCooking && cookingTime > 0) {
            cookingTime--;
            if (cookingTime <= 0) {
                finishCooking();
            }
        }
    }

    // Menyelesaikan proses pemanggangan
    private void finishCooking() {
        isCooking = false;
        
        // Cek bahan-bahan untuk menentukan jenis pizza
        if (dough == null) {
            isBurned = true;
            return;
        }
        
        try {
            // Cek resep pizza berdasarkan bahan yang ada
            if (dough.getCheese() != null && dough.getTomato() != null && 
                dough.getChicken() == null && dough.getSausage() == null) {
                // Resep Pizza Margherita: Dough + Cheese + Tomato
                cookedDish = new pizzaMargherita();
            } else if (dough.getCheese() != null && dough.getChicken() != null) {
                // Resep Pizza Ayam: Dough + Cheese + Chicken
                cookedDish = new pizzaAyam();
            } else if (dough.getCheese() != null && dough.getSausage() != null) {
                // Resep Pizza Sosis: Dough + Cheese + Sausage
                cookedDish = new pizzaSosis();
            } else {
                // Jika bahan tidak sesuai resep manapun, hasilnya gosong
                isBurned = true;
            }
        } catch (Exception e) {
            System.err.println("Error creating pizza: " + e.getMessage());
            isBurned = true;
        }
        
        // Reset dough setelah dimasak
        dough = null;
    }

    // Mengambil hasil masakan dari oven
    public Item takeOut() {
        if (isCooking) {
            return null; // Masih dimasak
        }
        
        if (isBurned) {
            isBurned = false;
            cookedDish = null;
            return null; // Makanan gosong, tidak bisa diambil
        }
        
        if (cookedDish != null) {
            Dish result = cookedDish;
            cookedDish = null;
            return result;
        }
        
        return null;
    }

    // Getter untuk status
    public boolean isCooking() {
        return isCooking;
    }

    public DoughFinalMixed getCurrentDough() {
        return dough;
    }

    public int getRemainingTime() {
        return cookingTime;
    }
    
    public boolean isBurned() {
        return isBurned;
    }
    
    public boolean hasFinishedCooking() {
        return !isCooking && (cookedDish != null || isBurned);
    }
}
