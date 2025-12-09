package model.item.ingredient.pizza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.enums.ItemLocation;
import model.item.ingredient.Ingredient;

public class Dough extends Ingredient {

    // daftar topping yang "nempel" di dough waktu di assembly
    private final List<Ingredient> toppings = new ArrayList<>();

    public Dough() {
        this(ItemLocation.INGREDIENT_STORAGE);
    }

    public Dough(ItemLocation location) {
        super("Dough", location);
    }

    /**
     * Topping boleh ditambah kalau:
     * - dough sudah CHOPPED
     * - topping CHOPPED
     * - bukan null
     * - belum ada di list
     */
    public boolean canAcceptTopping(Ingredient topping) {
        if (topping == null) return false;
        if (!this.isChopped()) return false;
        if (!topping.isChopped()) return false;
        if (topping == this) return false;
        return !toppings.contains(topping);
    }

    public boolean addTopping(Ingredient topping) {
        if (!canAcceptTopping(topping)) {
            System.out.println("[Dough] Tidak bisa menambahkan topping: " +
                               (topping != null ? topping.getName() : "null") +
                               " (cek: dough/topping harus CHOPPED, tidak duplikat).");
            return false;
        }
        toppings.add(topping);
        System.out.println("[Dough] Topping " + topping.getName() + " ditambahkan ke dough.");
        return true;
    }

    public List<Ingredient> getToppings() {
        return Collections.unmodifiableList(toppings);
    }

    public void clearToppings() {
        toppings.clear();
    }
}
