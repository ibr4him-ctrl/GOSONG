package model.logic;

import java.util.Collection;
import model.enums.IngredientState;
import model.item.Item;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Dough;
import model.item.utensils.Plate;

public final class PlatingRules {

    private PlatingRules() { }

    public static boolean applyPlating(Plate plate,
                                    Preparable ingredient,
                                    String contextTag) {

        if (plate == null || ingredient == null) {
            System.out.println(contextTag + " Plating gagal: plate / ingredient null.");
             return false;
        }

        // 1) Plate harus bersih
        if (!plate.isClean()) {
            System.out.println(contextTag + " Plate kotor tidak bisa digunakan untuk plating! Cuci dulu.");
            return false;
        }

        // 2) Cek general rule via Plate.canAccept()
        if (!plate.canAccept(ingredient)) {
            System.out.println(contextTag + " Ingredient tidak bisa ditambahkan ke plate (canAccept = false).");
            if (ingredient instanceof Ingredient ing && !ing.canBePlacedOnPlate()) {
                System.out.println(contextTag + "   Alasan: ingredient harus dipotong / dimasak dulu.");
            }
            return false;
        }

        // ====== INFO INGREDIENT & ISI PLATE ======
        Ingredient ingObj = (ingredient instanceof Ingredient) ? (Ingredient) ingredient : null;
        boolean isDough   = ingredient instanceof Dough;
        boolean isRaw     = (ingObj != null && ingObj.getState() == IngredientState.RAW);

        Collection<Preparable> contents = plate.getContents();
        boolean plateEmpty    = contents.isEmpty();
        boolean plateHasDough = contents.stream().anyMatch(p -> p instanceof Dough);
        boolean plateHasRaw   = contents.stream().anyMatch(p ->
                p instanceof Ingredient i2 && i2.getState() == IngredientState.RAW);

        // ====== RULE RAW ======
        if (isRaw && !plateEmpty) {
            System.out.println(contextTag +
                    " Ingredient RAW hanya boleh di-plate pada plate kosong, tidak bisa menjadi topping.");
            return false;
        }

        if (plateHasRaw && !plateEmpty) {
            System.out.println(contextTag +
                    " Plate ini sudah berisi ingredient RAW, tidak bisa menambahkan ingredient lain lagi.");
            return false;
        }

        // ====== ATURAN PIZZA (dough base + chopped topping) ======
        if (!plateEmpty) {
            if (!plateHasDough) {
                // Plate sudah berisi sesuatu tapi bukan dough → jenis lain, jangan dicampur
                System.out.println(contextTag +
                        " Plate ini sudah berisi ingredient tanpa dough, tidak bisa menambahkan ingredient lain lagi.");
                return false;
            } else {
                // NEW: pastikan dough di plate SUDAH CHOPPED sebelum boleh diberi topping
                Dough doughOnPlate = contents.stream()
                        .filter(p -> p instanceof Dough)
                        .map(p -> (Dough) p)
                        .findFirst()
                        .orElse(null);

                if (doughOnPlate != null &&
                    doughOnPlate.getState() != IngredientState.CHOPPED) {
                    System.out.println(contextTag +
                            " Dough harus CHOPPED dulu sebelum bisa ditambah topping.");
                    return false;
                }

                // Tidak boleh tambah dough lagi
                if (isDough) {
                    System.out.println(contextTag + " Plate sudah punya Dough, tidak bisa menambah Dough lagi.");
                    return false;
                }

                // Topping di atas dough HARUS chopped
                if (!(ingredient instanceof Ingredient topping) ||
                        topping.getState() != IngredientState.CHOPPED) {
                    System.out.println(contextTag +
                            " Topping di atas dough harus dalam state CHOPPED (bukan RAW / COOKED / BURNED).");
                    return false;
                }
            }
        }

        // 3) Eksekusi: tambah ke plate
        //    KHUSUS JIKA ingredient = Dough DENGAN TOPPING
        if (ingredient instanceof Dough dough) {

            var toppings = dough.getToppings();

            if (toppings.isEmpty()) {
                // Dough tanpa topping → perlakuan biasa
                boolean success = plate.addIngredient(dough);
                if (!success) {
                    System.out.println(contextTag + " Plate menolak Dough.");
                    return false;
                }
                logPlateContents(plate, ingredient, contextTag);
                return true;
            } else {
                // Dough PIZZA: base + semua topping ikut masuk ke plate
                System.out.println(contextTag + " Plating dough dengan topping → pindah ke plate sebagai beberapa ingredient.");

                boolean successBase = plate.addIngredient(dough);
                if (!successBase) {
                    System.out.println(contextTag + " Plate menolak Dough (base).");
                    return false;
                }

                for (Ingredient t : toppings) {
                    boolean ok = plate.addIngredient(t);
                    if (!ok) {
                        // kalau duplikat dll, kita log aja, tapi gak langsung fail total
                        System.out.println(contextTag +
                                " Plate menolak topping " + t.getName() +
                                " (mungkin duplikat).");
                    }
                }

                logPlateContents(plate, ingredient, contextTag);
                return true;
            }
        }

        // 3b) BUKAN dough → normal
        boolean success = plate.addIngredient(ingredient);
        if (!success) {
            System.out.println(contextTag + " Plate menolak ingredient (mungkin duplikat di set).");
            return false;
        }

        logPlateContents(plate, ingredient, contextTag);
        return true;

    }


    private static void logPlateContents(Plate plate,
                                         Preparable justAdded,
                                         String contextTag) {
        String ingName;
        if (justAdded instanceof Item it) {
            ingName = it.getName();
        } else {
            ingName = justAdded.getClass().getSimpleName();
        }

        System.out.println(contextTag + " Plating: " + ingName + " → Plate");
        System.out.println(contextTag + "   Plate sekarang berisi: " + plate.getContents().size() + " ingredient(s)");

        StringBuilder sb = new StringBuilder();
        sb.append(contextTag).append("   Isi plate: [");

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
        sb.append(']');
        System.out.println(sb.toString());
    }
}
