package model.station;

import model.chef.Chef;
import model.enums.IngredientState;
import model.item.Item;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Dough;
import model.item.utensils.Plate;
import model.logic.PlatingRules;

public class AssemblyStation extends Station {

    public AssemblyStation(int x, int y) {
        super(x, y, "Assembly");
    }

    @Override
    public String getSymbol() {
        return "A";
    }

    @Override
    public boolean interact(Chef chef) {

        if (!isAdjacentTo(chef)) {
            System.out.println("Chef terlalu jauh dari assembly station!");
            return false;
        }

        Item hand = chef.getHeldItem();
        Item top  = itemOnStation;

        // =====================================================
        // 1) PRIORITAS: PLATE + PREPARABLE (sesuai spek)
        // =====================================================

        // Plate di tangan, objek Preparable (Ingredient / Dough / Dish) di meja
        if (hand instanceof Plate plateInHand && top instanceof Preparable prepOnTable) {
            return doPlating(chef, plateInHand, prepOnTable, true);
        }

        // Plate di meja, objek Preparable di tangan
        if (top instanceof Plate plateOnTable && hand instanceof Preparable prepInHand) {
            return doPlating(chef, plateOnTable, prepInHand, false);
        }

        // =====================================================
        // 2) KELUAR JALUR: Dough di meja + Ingredient di tangan
        //    (assembling tanpa plate)
        // =====================================================

        if (top instanceof Dough doughOnTable && hand instanceof Ingredient ingInHand) {
            return combineDoughAndTopping(chef, doughOnTable, ingInHand);
        }

        // =====================================================
        // 3) INTERAKSI BIASA: ambil / taruh 1 item di meja
        // =====================================================

        // Tangan kosong, meja ada item → ambil

        if (hand == null && top != null) {
            
            if (top instanceof Dough dough) {
                // anggap Dough punya getToppings() yang return Collection/Set<String> atau sejenisnya
                boolean hasTopping = dough.getToppings() != null && !dough.getToppings().isEmpty();
                boolean isChopped  = dough.getState() == IngredientState.CHOPPED;

                if (isChopped && hasTopping) {
                    System.out.println("[Assembly] Pizza (dough + topping) tidak boleh diambil tangan kosong. " +
                                    "Gunakan plate bersih untuk mengambilnya.");
                    return false;
                }
            }

            // selain case di atas → boleh diambil seperti biasa
            chef.setHeldItem(top);
            itemOnStation = null;
            System.out.println("Chef " + chef.getName() + " mengambil "
                    + chef.getHeldItem().getName() + " dari assembly.");
            return true;
        }

        // Tangan pegang item, meja kosong → taruh
        if (hand != null && top == null) {

            // plate kotor TIDAK boleh ditaruh di assembly
            if (hand instanceof Plate p && !p.isClean()) {
                System.out.println("[Assembly] Plate kotor tidak boleh ditaruh di assembly.");
                return false;
            }

            // selain itu (dough, ingredient, dish, plate bersih, dll) → boleh
            itemOnStation = hand;
            chef.setHeldItem(null);
            System.out.println("Chef " + chef.getName() + " meletakkan "
                    + itemOnStation.getName() + " di assembly.");
            return true;
        }

        // =====================================================
        // 4) CASE LAIN: GAGAL (tangan isi & meja isi,
        //    bukan kombinasi yang didukung)
        // =====================================================

        if (hand != null && top != null) {
            System.out.println("[Assembly] Udah ada item di assembly, "
                    + "gabisa naro lagi kecuali kombinasi:"
                    + " Plate + Ingredient/Dough/Dish atau Dough + Ingredient (tanpa plate).");
        }

        return false;
    }

    // =======================
    // HELPER: PLATING NORMAL
    // =======================
    private boolean doPlating(Chef chef,
                              Plate plate,
                              Preparable prep,
                              boolean plateInHand) {

        boolean ok = PlatingRules.applyPlating(plate, prep, "[Assembly]");
        if (!ok) return false;

        if (plateInHand) {
            // Plate di tangan, prep di meja → prep dihapus dari meja
            itemOnStation = null;
            chef.setHeldItem(plate);
        } else {
            // Plate di meja, prep di tangan → tangan dikosongkan
            chef.setHeldItem(null);
            itemOnStation = plate;
        }

        return true;
    }

    // =======================
    // HELPER: DOUGH + TOPPING
    // =======================
    private boolean combineDoughAndTopping(Chef chef,
                                           Dough doughOnTable,
                                           Ingredient toppingInHand) {

        if (!doughOnTable.isChopped()) {
            System.out.println("[Assembly] Dough harus CHOPPED sebelum bisa ditambah topping.");
            return false;
        }
        if (!toppingInHand.isChopped()) {
            System.out.println("[Assembly] Topping harus CHOPPED sebelum bisa digabung ke dough.");
            return false;
        }

        boolean ok = doughOnTable.addTopping(toppingInHand);
        if (!ok) {
            // Pesan detail sudah dikeluarkan oleh Dough.addTopping()
            return false;
        }

        // topping pindah dari tangan → “nempel” di dough
        chef.setHeldItem(null);
        System.out.println("[Assembly] " + toppingInHand.getName()
                + " ditambahkan ke Dough di assembly (tanpa plate).");
        return true;
    }

    // =======================
    // UTIL LAIN
    // =======================
    public Item takeItem() {
        Item temp = itemOnStation;
        itemOnStation = null;
        return temp;
    }

    public boolean placeItem(Item item) {
        if (itemOnStation != null || item == null) return false;

        if (item instanceof Plate p && !p.isClean()) {
            System.out.println("[Assembly] Plate kotor tidak boleh ditaruh di assembly (placeItem).");
            return false;
        }

        itemOnStation = item;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return itemOnStation == null;
    }

    @Override
    public String toString() {
        return String.format("AssemblyStation{pos=(%d,%d), item=%s}",
                posX, posY,
                itemOnStation != null ? itemOnStation.getName() : "empty");
    }
}
