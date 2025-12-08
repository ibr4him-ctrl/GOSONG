package model.station;

import model.chef.Chef;
import model.enums.IngredientState;
import model.item.Item;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Dough;
import model.item.utensils.Plate;

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

        // 1) PRIORITAS: PLATING
        
        // Plate di tangan, ingredient di meja
        if (hand instanceof Plate plate && top instanceof Ingredient ing) {
            return performPlating(chef, plate, ing, true);
        }

        // Ingredient di tangan, plate di meja
        if (hand instanceof Ingredient ing && top instanceof Plate plate) {
            return performPlating(chef, plate, ing, false);
        }


        // 2) AMBIL / TARUH BIASA

        // Tangan kosong, meja ada item → ambil
        if (hand == null && top != null) {
            chef.setHeldItem(top);
            itemOnStation = null;
            System.out.println("Chef " + chef.getName() + " mengambil " +
                               chef.getHeldItem().getName() + " dari assembly.");
            return true;
        }

        // Tangan pegang item, meja kosong → taruh
        if (hand != null && top == null) {
            if (hand instanceof Ingredient /* || hand instanceof Dish */) {
            System.out.println(
                "[Assembly] Ingredient (atau makanan) harus di atas plate, " +
                "tidak bisa ditaruh langsung di Station!"
                );
            return false;
            }
            itemOnStation = hand;
            chef.setHeldItem(null);
            System.out.println("Chef " + chef.getName() + " meletakkan " +
                               itemOnStation.getName() + " di assembly.");
            return true;
        }

        // 3) CASE LAIN: GAGAL
        

        if (hand != null && top != null) {
            System.out.println("Udah ada item di atas assembly, " +
                               "gabisa naro lagi kecuali kombinasi Plate + Ingredient.");
        }
        return false;
    }

    private boolean performPlating(Chef chef,
                                   Plate plate,
                                   Preparable ingredient,
                                   boolean plateInHand) {

        // Plate harus bersih
        if (!plate.isClean()) {
            System.out.println("Plate kotor tidak bisa digunakan untuk plating! Cuci dulu di Washing Station.");
            return false;
        }

        // Ingredient harus boleh di-plate
        if (!plate.canAccept(ingredient)) {
            System.out.println("Ingredient tidak bisa ditambahkan ke plate!");
            if (ingredient == null) {
                System.out.println("Loh gada ingredientnya.");
            } else if (!ingredient.canBePlacedOnPlate()) {
                System.out.println("Ingredient harus dipotong / dimasak dulu.");
            }
            return false;
        }

        // ====== INFO INGREDIENT & ISI PLATE ======
        Ingredient ingObj = (ingredient instanceof Ingredient) ? (Ingredient) ingredient : null;
        boolean isDough   = ingredient instanceof Dough;
        boolean isRaw     = (ingObj != null && ingObj.getState() == IngredientState.RAW);

        var contents       = plate.getContents();
        boolean plateEmpty = contents.isEmpty();
        boolean plateHasDough = contents.stream().anyMatch(p -> p instanceof Dough);
        boolean plateHasRaw   = contents.stream().anyMatch(p ->
                p instanceof Ingredient i2 && i2.getState() == IngredientState.RAW);


            // ====== RULE RAW: raw boleh masuk plate, tapi TIDAK bisa digabung ======

        // Kalau mau menambah RAW ke plate yang sudah ada isinya → tolak
        if (isRaw && !plateEmpty) {
            System.out.println("Ingredient RAW hanya boleh di-plate pada plate kosong, tidak bisa digabung.");
            return false;
        }

        // Kalau plate sudah berisi ingredient RAW → tolak penambahan apapun
        if (plateHasRaw && !plateEmpty) {
            System.out.println("Plate ini sudah berisi ingredient RAW, tidak bisa menambahkan ingredient lain lagi.");
            return false;
        }

        // ====== ATURAN PIZZA (dough base) ======
        // CASE 1: plate kosong -> apapun boleh (dough / topping)
        if (plateEmpty) {
            // tidak ada aturan ekstra
        } else {
            // plate TIDAK kosong
            if (!plateHasDough) {
                // artinya ingredient pertama BUKAN dough → single topping only
                System.out.println(
                    "Plate ini sudah berisi ingredient tanpa dough, " +
                    "tidak bisa menambahkan ingredient lain lagi."
                );
                return false;
            } else {
                // plate sudah punya dough
                if (isDough) {
                    System.out.println("Plate sudah punya Dough, tidak bisa menambah Dough lagi.");
                    return false;
                }
                // kalau topping lain (Tomato/Cheese/Chicken/Sausage) → boleh
            }
        }
        // ====== END ATURAN PIZZA ======


        // Tambahkan ke plate
        boolean success = plate.addIngredient(ingredient);
    
        if (!success) {
            System.out.println("Ingredient mungkin sudah ada di plate (set menolak duplikat).");
            return false;
        }

        String ingName = (ingredient instanceof Item i) ? i.getName() : "Ingredient";
        
        System.out.println("Plating: " + ingName + " → Plate");
        // log isi plate biar kamu kelihatan
        System.out.print("   Plate sekarang berisi: ");
        if (plate.getContents().isEmpty()) {
            System.out.println("(kosong?)");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Preparable p : plate.getContents()) {
                if (p instanceof Item it) {
                    sb.append(it.getName());
                } else {
                    sb.append(p.getClass().getSimpleName());
                }
                sb.append(", ");
            }
            // buang koma terakhir
            if (sb.length() >= 2) sb.setLength(sb.length() - 2);
            System.out.println(sb.toString());
        }
        
        if (plateInHand) {
            // Plate di tangan, ingredient dari meja
            itemOnStation = null;      // hapus ingredient dari meja
            chef.setHeldItem(plate);   // plate tetap di tangan
        } else {
            // Plate di meja, ingredient dari tangan
            chef.setHeldItem(null);    // tangan kosong
            itemOnStation = plate;     // plate tetap di meja
        }

        System.out.println("   Plate sekarang berisi: " + plate.getContents().size() + " ingredient(s)");
        StringBuilder sb = new StringBuilder("   Isi plate: [");
        boolean first = true;
        for (Preparable prep : plate.getContents()) {
            String name;
            if (prep instanceof Item it) {
                name = it.getName();
            } else {
                name = prep.getClass().getSimpleName();
            }

            if (!first) sb.append(", ");
            sb.append(name);

            if (prep instanceof Ingredient ing2) {
                sb.append(" (").append(ing2.getState()).append(")");
            }
            first = false;
        }
        sb.append("]");
        System.out.println(sb.toString());
        

        return true;


    }

    public Item takeItem() {
        Item temp = itemOnStation;
        itemOnStation = null;
        return temp;
    }

    public boolean placeItem(Item item) {
        if (itemOnStation != null) return false;
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
