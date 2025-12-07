package model.station;

import java.util.ArrayList;
import java.util.List;
import model.chef.Chef;
import model.enums.PizzaType;
import model.item.Dish;
import model.item.Item;
import model.item.Preparable;
import model.item.dish.PizzaRecipeChecker;
import model.item.ingredient.Ingredient;
import model.item.utensils.Oven;
import model.item.utensils.Plate;

public class CookingStation extends Station {

    private final Oven oven;

    public CookingStation(int x, int y) {
        super(x, y, "Cooking");
        this.oven = new Oven();
    }

    @Override
    public String getSymbol() {
        return "R";
    }

    public Oven getOven() {
        return oven;
    }

    //SUMPAH PLS KENAPA OVEN NYEBELIN WATDEFAK 
    
    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false;

        Item hand = chef.getHeldItem();

        // =========================
        // 1) OVEN SUDAH SIAP & CHEF PEGANG PLATE → KELUARKAN PIZZA
        // =========================
        if (oven.isReadyToTakeOut() && hand instanceof Plate) {

            Plate plate = (Plate)hand; 

            
            if (!plate.isClean()) {
                System.out.println("[CookingStation] Plate kotor, gak boleh buat plating pizza matang.");
                return false;
            }

            System.out.println("[CookingStation] Mengeluarkan isi oven...");
            List<Preparable> cooked = oven.takeOutAll();  // aman: >= COOK_TIME_DONE
            
            //deteksi jenis pizza dari kombinasi cooked 

            PizzaType type = PizzaRecipeChecker.detectPizza(cooked);

            Dish dish = PizzaRecipeChecker.createPizzaDish(type, cooked);

            if (dish != null) {
                // Pastikan Dish boleh ditaruh ke plate --> debugging plate 
                if (!plate.canAccept(dish)) {
                    System.out.println("[CookingStation] Dish final tidak bisa ditaruh ke plate (canAccept() = false).");
                    return false;
                }
                plate.addIngredient(dish);
                chef.setHeldItem(plate);   
                System.out.println("[CookingStation] Pizza jadi: " + dish.getName());
                System.out.println("   Isi plate sekarang: " + plate.getContents().size() + " item");
                return true;
            }

            System.out.println("[CookingStation] Kombinasi cooked tidak cocok resep mana pun.");
            return false;
        }

        // =========================
        // 2) CHEF PEGANG PLATE → KIRIM ISI PLATE KE OVEN
        // =========================
        if (hand instanceof Plate plate) {

            // kalau oven lagi masak, jangan ganggu / tambah ingredient
            if (oven.isCooking()) {
                System.out.println("Oven sedang memasak, tidak bisa tambah bahan.");
                return false;
            }

            boolean movedAny = false;
            var contentsCopy = new ArrayList<>(plate.getContents());

            for (var p : contentsCopy) {
                if (!(p instanceof Ingredient ing)) continue;   // cuma Ingredient
                if (!oven.canAccept(ing)) continue;            // oven full / gak boleh

                oven.addIngredient(ing);
                plate.removeIngredient(ing);                   // pastikan ada di Plate

                movedAny = true;
                System.out.println("Masukin dari plate ke oven: " + ing.getName());
            }

            if (movedAny && !oven.isCooking()) {
                oven.startCooking();
                System.out.println("Oven start cooking, isi = " + oven.getContents().size());
            }

            return movedAny;  // true kalau ada yang masuk, false kalau plate kosong / gagal
        }

        // =========================
        // 3) CHEF PEGANG INGREDIENT LANGSUNG → MASUKKAN KE OVEN
        // =========================
        if (hand instanceof Ingredient ing) {
            if (!oven.canAccept(ing)) {
                System.out.println("Oven menolak ingredient: " + ing.getName());
                return false;
            }

            oven.addIngredient(ing);
            chef.setHeldItem(null);

            if (!oven.isCooking()) {
                oven.startCooking();
                System.out.println("Oven start cooking, isi = " + oven.getContents().size());
            }
            return true;
        }

        // =========================
        // 4) CASE LAIN: GAK NGAPA-NGAPAIN
        // =========================
        return false;
    }

    public void update(double deltaSeconds) {
        oven.update(deltaSeconds);
    }
}
