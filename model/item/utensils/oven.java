package model.item.utensils;

import model.item.Item;
import model.item.ingredient.pizza.Dough;
import model.item.ingredient.pizza.DoughFinalMixed;

public class oven extends Item {
    private DoughFinalMixed dough; // Adonan yang sedang dimasak
    private int cookingTime; // Waktu memasak tersisa
    private boolean isCooking; // Status sedang memasak
    private static final int DEFAULT_COOKING_TIME = 5; // Waktu standar memasak
    private boolean isBurned; // Status apakah makanan gosong

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

    // Menyelesaikan pemanggangan
    private void finishCooking() {
        if (Dough != null) {
            // Menentukan kualitas hasil pemanggangan berdasarkan kualitas adonan dan waktu memasak
            int quality = Dough.getQuality();
            
            // Jika waktu memasak melebihi batas, makanan menjadi gosong
            if (cookingTime < -2) {
                isBurned = true;
                Dough = new Item("Burned Bread", Item.ItemType.DISH, Item.ItemLocation.COUNTER);
                Dough.setEdible(false); // Makanan gosong tidak bisa dimakan
            } else {
                // Buat makanan matang berdasarkan kualitas adonan
                String foodName = quality > 70 ? "Perfect Bread" : 
                                quality > 40 ? "Bread" : "Burnt Bread";
                dough = new Item(foodName, Item.ItemType.DISH, Item.ItemLocation.COUNTER);
                dough.setEdible(quality > 40); // Hanya bisa dimakan jika kualitas cukup
                isBurned = quality <= 40;
            }
            isCooking = false;
        }
    }

    // Mengambil makanan yang sudah matang
    public Item takeFood() {
        if (dough != null && !isCooking) {
            Item cookedFood = dough;
            dough = null;
            return cookedFood;
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
        return !isCooking && dough != null && !(dough instanceof DoughFinalMixed);
    }
}
