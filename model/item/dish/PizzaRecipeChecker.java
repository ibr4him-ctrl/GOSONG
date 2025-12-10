package model.item.dish;

import java.util.List;
import model.enums.IngredientState;
import model.enums.ItemLocation;
import model.enums.PizzaType;
import model.item.Dish;
import model.item.Preparable;
import model.item.ingredient.Ingredient;
import model.item.ingredient.pizza.Cheese;
import model.item.ingredient.pizza.Chicken;
import model.item.ingredient.pizza.Dough;
import model.item.ingredient.pizza.Sausage;
import model.item.ingredient.pizza.Tomato;

public class PizzaRecipeChecker {

    private PizzaRecipeChecker() {
        // utility class, jangan di-instansiasi
    }

    /**
     * Deteksi ini pizza apa dari list ingredient yang SUDAH KELUAR OVEN.
     * Aturan:
     * - Semua komponen harus Ingredient dan ber-state COOKED
     * - Komponen pertama HARUS Dough
     * - Tomato & Cheese wajib ada
     * - Sosis / Ayam opsional tergantung tipe pizza
     * - Urutan topping (setelah dough) boleh acak
     */
    public static PizzaType detectPizza(List<Preparable> components) {
        if (components == null || components.isEmpty()) {
            return null;
        }

        // ====== CEK KOMPONEN PERTAMA: HARUS DOUGH COOKED ======
        Preparable first = components.get(0);
        if (!(first instanceof Ingredient firstIng)) {
            System.out.println("[PizzaRecipeChecker] Komponen pertama bukan Ingredient.");
            return null;
        }
        if (!(firstIng instanceof Dough)) {
            System.out.println("[PizzaRecipeChecker] Komponen pertama bukan Dough.");
            return null;
        }
        if (firstIng.getState() != IngredientState.COOKED) {
            System.out.println("[PizzaRecipeChecker] Dough pertama tidak dalam state COOKED.");
            return null;
        }

        boolean hasDough   = false;
        boolean hasTomato  = false;
        boolean hasCheese  = false;
        boolean hasChicken = false;
        boolean hasSausage = false;

        int ingredientCount = 0;

        int index = 0;
        for (Preparable p : components) {
            if (!(p instanceof Ingredient ing)) {
                System.out.println("[PizzaRecipeChecker] Ada komponen non-Ingredient, recipe gagal.");
                return null;
            }

            ingredientCount++;

            // Semua bahan keluar dari oven harus COOKED
            IngredientState state = ing.getState();
            if (state != IngredientState.COOKED) {
                System.out.println("[PizzaRecipeChecker] Ingredient bukan COOKED: "
                        + ing.getName() + " state=" + state);
                return null;
            }

            // index 0 sudah dicek di atas, tapi sekalian jaga-jaga
            if (index == 0 && !(ing instanceof Dough)) {
                System.out.println("[PizzaRecipeChecker] index 0 bukan Dough (inconsistent).");
                return null;
            }

            if ( ing instanceof Dough )   hasDough   = true;
            if ( ing instanceof Tomato )  hasTomato  = true;
            if ( ing instanceof Cheese )  hasCheese  = true;
            if ( ing instanceof Chicken ) hasChicken = true;
            if ( ing instanceof Sausage ) hasSausage = true;

            index++;
        }

        // Kalau nggak ada dough sama sekali, langsung gagal
        if (!hasDough) {
            return null;
        }

        // ====== COBA COCOKKAN DENGAN RECIPE ======

        // Margherita: Dough + Tomato + Cheese
        if (ingredientCount == 3 &&
            hasDough && hasTomato && hasCheese &&
            !hasChicken && !hasSausage) {

            return PizzaType.MARGHERITA;
        }

        // Sosis: Dough + Tomato + Cheese + Sausage
        if (ingredientCount == 4 &&
            hasDough && hasTomato && hasCheese &&
            !hasChicken && hasSausage) {

            return PizzaType.SOSIS;
        }

        // Ayam: Dough + Tomato + Cheese + Chicken
        if (ingredientCount == 4 &&
            hasDough && hasTomato && hasCheese &&
            hasChicken && !hasSausage) {

            return PizzaType.AYAM;
        }

        // kombinasi nggak cocok recipe apapun
        return null;
    }

    // bikin objek dish pizza konkret dari tipe + komponennya
    public static Dish createPizzaDish(PizzaType type, List<Preparable> components) {
        if (type == null) {
            return null;
        }

        Dish dish;

        switch (type) {
            case MARGHERITA -> dish = new PizzaMargherita(ItemLocation.PLATING);
            case AYAM       -> dish = new PizzaAyam(ItemLocation.PLATING);
            case SOSIS      -> dish = new PizzaSosis(ItemLocation.PLATING);
            default -> {
                return null;
            }
        }

        if (components != null) {
            for (Preparable p : components) {
                dish.addComponent(p);
            }
        }
        return dish;
    }
}
