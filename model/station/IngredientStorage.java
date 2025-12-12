package model.station;

import java.lang.reflect.Constructor;
import model.chef.Chef;
import model.enums.IngredientState;
import model.enums.ItemLocation;
import model.item.Item;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Dough;
import model.item.utensils.Plate;
import model.logic.PlatingRules;
import util.SoundEffectPlayer;

public class IngredientStorage extends Station {

    private Class<? extends Ingredient> ingredientType;
    private String ingredientName;

    // ===== SFX =====
    private static final SoundEffectPlayer SFX = new SoundEffectPlayer();
    private static final String SFX_PICKUPDROP =
            "/resources/game/sound_effect/pickupdrop.wav";

    public IngredientStorage(int x, int y) {
        super(x, y, "IngredientStorage");
    }

    /**
     * Constructor utama yang dipakai dari GamePanel.initStationsFromMap()
     */
    public IngredientStorage(int x, int y,
                             Class<? extends Ingredient> ingredientType,
                             String ingredientName) {
        super(x, y, "IngredientStorage");
        this.ingredientType = ingredientType;
        this.ingredientName = ingredientName;
    }

    @Override
    public String getSymbol() {
        return "I";
    }

    // ============================
    //  HELPER: SPAWN NEW INGREDIENT
    // ============================
    private Ingredient spawnNewIngredient() {
        if (ingredientType == null) {
            System.err.println("[IngredientStorage] ingredientType null di (" + posX + "," + posY + ")");
            return null;
        }
        try {
            // Coba ctor kosong dulu (misal Dough())
            try {
                Constructor<? extends Ingredient> c0 = ingredientType.getDeclaredConstructor();
                c0.setAccessible(true);
                Ingredient ing = c0.newInstance();
                ing.setLocation(ItemLocation.INGREDIENT_STORAGE);
                return ing;
            } catch (NoSuchMethodException e) {
                // fallback: ctor dengan ItemLocation
                Constructor<? extends Ingredient> c1 =
                        ingredientType.getDeclaredConstructor(ItemLocation.class);
                c1.setAccessible(true);
                return c1.newInstance(ItemLocation.INGREDIENT_STORAGE);
            }
        } catch (Exception e) {
            System.err.println("[IngredientStorage] Gagal spawn ingredient " +
                               ingredientType.getSimpleName());
            e.printStackTrace();
            return null;
        }
    }

    // ============================
    //  HELPER: PLATING (PLATE + PREPARABLE)
    // ============================
    private boolean doPlating(Chef chef,
                              Plate plate,
                              Preparable ingredient,
                              boolean plateInHand) {

        boolean ok = PlatingRules.applyPlating(plate, ingredient, "[IngredientStorage]");
        if (!ok) return false;

        if (plateInHand) {
            // Plate di tangan, ingredient di meja → ingredient hilang dari meja
            itemOnStation = null;
            chef.setHeldItem(plate);
        } else {
            // Plate di meja, ingredient di tangan → tangan kosong, plate tetap di meja
            chef.setHeldItem(null);
            itemOnStation = plate;
        }

        SFX.playOnce(SFX_PICKUPDROP);

        return true;
    }

    @Override
    public boolean interact(Chef chef) {

        if (!isAdjacentTo(chef)) {
            System.out.println("Chef terlalu jauh dari ingredient storage!");
            return false;
        }

        Item hand = chef.getHeldItem();
        Item top  = itemOnStation;

        // =====================================================
        // 1) PRIORITAS: PLATE + PREPARABLE (kayak Assembly)
        // =====================================================

        // Plate di tangan, makanan di meja
        if (hand instanceof Plate plateInHand && top instanceof Preparable prepOnTable) {
            return doPlating(chef, plateInHand, prepOnTable, true);
        }

        // Makanan di tangan, plate di meja
        if (top instanceof Plate plateOnTable && hand instanceof Preparable prepInHand) {
            return doPlating(chef, plateOnTable, prepInHand, false);
        }

        // =====================================================
        // 2) DOUGH + TOPPING CHOPPED TANPA PLATE (assembling di atas I)
        // =====================================================

        // Dough di meja, topping di tangan
        if (top instanceof Dough doughOnTable && hand instanceof Ingredient ingInHand) {

            if (!doughOnTable.isChopped()) {
                System.out.println("[IngredientStorage] Dough harus CHOPPED dulu sebelum ditambah topping.");
                return false;
            }
            if (!ingInHand.isChopped()) {
                System.out.println("[IngredientStorage] Topping harus CHOPPED untuk digabung ke dough.");
                return false;
            }

            boolean ok = doughOnTable.addTopping(ingInHand);
            if (!ok) {
                // Pesan detail sebaiknya dicetak di Dough.addTopping()
                return false;
            }

            chef.setHeldItem(null); // topping pindah ke dough
            SFX.playOnce(SFX_PICKUPDROP);
            System.out.println("[IngredientStorage] " + ingInHand.getName() +
                               " ditambahkan ke Dough yang ada di storage.");
            return true;
        }

        // Dough di tangan, topping di meja
        if (hand instanceof Dough doughInHand && top instanceof Ingredient ingOnTable) {

            if (!doughInHand.isChopped()) {
                System.out.println("[IngredientStorage] Dough harus CHOPPED dulu sebelum ditambah topping.");
                return false;
            }
            if (!ingOnTable.isChopped()) {
                System.out.println("[IngredientStorage] Topping harus CHOPPED untuk digabung ke dough.");
                return false;
            }

            boolean ok = doughInHand.addTopping(ingOnTable);
            if (!ok) {
                return false;
            }

            // topping di meja sudah “ke-attach” ke dough → hilangkan dari meja
            itemOnStation = null;

            SFX.playOnce(SFX_PICKUPDROP);
            System.out.println("[IngredientStorage] " + ingOnTable.getName() +
                               " ditambahkan ke Dough yang dipegang chef.");
            return true;
        }

        // =====================================================
        // 3) BEHAVIOUR STORAGE NORMAL (spawn / taruh / ambil)
        // =====================================================

        if (hand == null) {

            // Kalau ada item di atas storage → coba ambil
            if (top != null) {

                // BLK KHUSUS: pizza base (dough chopped + topping) gak boleh diambil tangan kosong
                if (top instanceof Dough dough) {
                    boolean hasTopping = dough.getToppings() != null && !dough.getToppings().isEmpty();
                    boolean isChopped  = dough.getState() == IngredientState.CHOPPED;

                    if (isChopped && hasTopping) {
                        System.out.println("[IngredientStorage] Pizza (dough + topping) tidak boleh diambil tangan kosong. " +
                                        "Gunakan plate bersih untuk mengambilnya.");
                        return false;
                    }
                }

                // selain pizza base → boleh diambil biasa
                chef.setHeldItem(top);
                itemOnStation = null;
                SFX.playOnce(SFX_PICKUPDROP);
                System.out.println("[IngredientStorage] Chef " + chef.getName() +
                                " mengambil " + top.getName() + " dari atas storage.");
                return true;
            }

            // Tidak ada item di atas → spawn ingredient baru dari crate
            Ingredient spawned = spawnNewIngredient();
            if (spawned == null) {
                System.out.println("[IngredientStorage] Gagal spawn ingredient (null).");
                return false;
            }
            chef.setHeldItem(spawned);
            SFX.playOnce(SFX_PICKUPDROP);
            System.out.println("[IngredientStorage] Chef " + chef.getName() +
                            " mengambil " + spawned.getName() + " (RAW) dari storage.");
            return true;
        }

        // 3b) Tangan pegang sesuatu, meja kosong → taruh di “counter” storage
        if (hand != null && top == null) {

            // plate kotor tidak boleh
            if (hand instanceof Plate p && !p.isClean()) {
                System.out.println("[IngredientStorage] Plate kotor tidak boleh ditaruh di atas ingredient storage.");
                return false;
            }

            // selain itu (dough, ingredient apapun, dish, plate bersih, dll) → boleh ditaruh
            itemOnStation = hand;
            chef.setHeldItem(null);
            SFX.playOnce(SFX_PICKUPDROP);
            System.out.println("[IngredientStorage] Chef " + chef.getName() +
                               " meletakkan " + itemOnStation.getName() +
                               " di atas ingredient storage.");
            return true;
        }

        // =====================================================
        // 4) CASE LAIN: GAGAL (tangan isi & meja isi tapi bukan kombinasi yang didukung)
        // =====================================================
        if (hand != null && top != null) {
            System.out.println("[IngredientStorage] Di atas storage sudah ada item (" +
                               top.getName() + "), gabisa naro lagi kecuali kombinasi " +
                               "Plate + Preparable atau Dough + topping chopped.");
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return itemOnStation == null;
    }

    public Class<? extends Ingredient> getIngredientType() {
        return ingredientType;
    }

    public String getIngredientName() {
        return ingredientName;
    }


    @Override
    public String toString() {
        return String.format("IngredientStorage{pos=(%d,%d), type=%s, item=%s}",
                posX, posY,
                (ingredientType != null ? ingredientType.getSimpleName() : "null"),
                itemOnStation != null ? itemOnStation.getName() : "empty");
    }
    public Ingredient previewIngredientForUI() {
        return spawnNewIngredient(); // pakai logic spawn yang udah aman (ctor kosong / ItemLocation)
    }
}
