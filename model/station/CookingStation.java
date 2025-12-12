package model.station;

import java.util.ArrayList;
import java.util.List;
import model.chef.Chef;
import model.enums.IngredientState;
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
    
    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false;

        Item hand = chef.getHeldItem();

        // =========================
        // 1) OVEN SUDAH SIAP & CHEF PEGANG PLATE → KELUARKAN ISI
        // =========================
        if (oven.isReadyToTakeOut() && hand instanceof Plate) {

            Plate plate = (Plate) hand;

            if (!plate.isClean()) {
                System.out.println("[CookingStation] Plate kotor, gak boleh buat plating pizza matang.");
                return false;
            }

            boolean wasBurned = oven.isBurned();

            // ---- LIHAT DULU ISI OVEN TANPA NGOSONGIN ----
            List<Preparable> cookedView = new ArrayList<>(oven.getContents());
            System.out.println("[CookingStation] Siap mengeluarkan isi oven, count = " + cookedView.size()
                            + ", burned=" + wasBurned);

            // ========== KASUS 1: ISI OVEN GOSONG ==========
            if (wasBurned) {
                // Semua isi oven gosong → tetap dipindahin ke plate
                List<Preparable> cooked = oven.takeOutAll();   // baru beneran kosongin

                System.out.println("[CookingStation] Isi oven gosong! Dipindahkan ke plate untuk dibuang.");
                for (Preparable p : cooked) {
                    if (!plate.canAccept(p)) {
                        System.out.println("[CookingStation]   Tidak bisa menaruh " + p + " ke plate (canAccept = false).");
                        continue;
                    }
                    plate.addIngredient(p);
                }
                chef.setHeldItem(plate);
                System.out.println("   Plate sekarang berisi " + plate.getContents().size() + " item (burned).");
                return true;
            }

            // ========== KASUS 2: NORMAL, COBA JADI PIZZA DULU ==========
            PizzaType type = PizzaRecipeChecker.detectPizza(cookedView);
            Dish dish = PizzaRecipeChecker.createPizzaDish(type, cookedView);

            if (dish != null) {
                //  RESEP VALID → SET DISH KE PLATE
                oven.takeOutAll(); // Kosongkan oven
                plate.setDish(dish); // ← FIX: Pakai setDish() bukan addIngredient()
                chef.setHeldItem(plate);
                
                System.out.println("[CookingStation]  Pizza jadi: " + dish.getName());
                System.out.println("[CookingStation] Dish berhasil ditaruh di plate!");
                System.out.println("[CookingStation] plate.getDish() = " + plate.getDish());
                return true;
            }

            // ========== KASUS 3: RESEP TIDAK COCOK SAMA SEKALI ==========
            System.out.println("[CookingStation] Kombinasi cooked tidak cocok resep mana pun.");
            System.out.println("   Tetap dipindahkan ke plate sebagai 'gagal'.");

            List<Preparable> cooked = oven.takeOutAll();
            for (Preparable p : cooked) {
                if (!plate.canAccept(p)) {
                    System.out.println("[CookingStation]   Tidak bisa menaruh " + p + " ke plate (canAccept = false).");
                    continue;
                }
                plate.addIngredient(p);
            }
            chef.setHeldItem(plate);
            System.out.println("   Plate sekarang berisi " + plate.getContents().size() + " item (hasil gagal).");
            return true;
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

            // RULE : plate harus mengandung Dough
            if (!plateHasDough(plate)) {
                System.out.println("[CookingStation] Plate tanpa dough tidak boleh masuk oven.");
                return false;
            }

            //RULE : plate tidak boleh mengandung INGREDIENT RAW
            if (plateHasRawIngredient(plate)) {
                System.out.println("[CookingStation] Plate masih berisi ingredient RAW, tidak boleh masuk oven.");
                return false;
            }

            boolean movedAny = false;
            var contentsCopy = new ArrayList<>(plate.getContents());

            for (var p : contentsCopy) {
                if (!(p instanceof Ingredient ing)) continue;

                // (opsional, bisa tambah guard lagi di sini)
                if (ing.getState() == IngredientState.RAW) {
                    System.out.println("[CookingStation] Skip ingredient RAW: " + ing.getName());
                    continue;
                }

                if (!oven.canAccept(ing)) continue;

                oven.addIngredient(ing);
                plate.removeIngredient(ing);

                movedAny = true;
                System.out.println("Masukin dari plate ke oven: " + ing.getName());
            }

            if (movedAny && !oven.isCooking()) {
                oven.startCooking();
                System.out.println("Oven start cooking, isi = " + oven.getContents().size());
            }

            return movedAny;
        }

        // =========================
        // 3) CHEF PEGANG INGREDIENT LANGSUNG → MASUKKAN KE OVEN
        // =========================
        if (hand instanceof Ingredient ing) {

            if (ing.getState() == IngredientState.RAW) {
                System.out.println("[CookingStation] Tidak boleh memasukkan ingredient RAW langsung ke oven.");
                return false;
            }

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

    private boolean plateHasDough(Plate plate) {
        for (Preparable p : plate.getContents()) {
            if (p instanceof Ingredient ing && ing instanceof model.item.ingredient.pizza.Dough) {
                return true;
            }
        }
        return false;
    }
    
    private boolean plateHasRawIngredient(Plate plate) {
        for (Preparable p : plate.getContents()) {
            if (p instanceof Ingredient ing) {
                if (ing.getState() == IngredientState.RAW) {
                    return true;
                }
            }
        }
        return false;
    }
}