import java.util.List;
import model.enums.ItemLocation;
import model.enums.PizzaType;
import model.item.Dish;
import model.item.Preparable;
import model.item.dish.PizzaRecipeChecker;
import model.item.ingredient.pizza.Cheese;
import model.item.ingredient.pizza.Dough;
import model.item.ingredient.pizza.Tomato;
import model.item.utensils.Oven;

public class OvenTest {
    public static void main(String[] args) {

        // 1) Bikin oven
        Oven oven = new Oven();

        // 2) Bikin bahan Pizza Margherita: Dough + Tomato + Cheese
        Dough dough   = new Dough(ItemLocation.INGREDIENT_STORAGE);
        Tomato tomato = new Tomato(ItemLocation.INGREDIENT_STORAGE);
        Cheese cheese = new Cheese(ItemLocation.INGREDIENT_STORAGE);

        // harus CHOPPED supaya canAccept() = true
        dough.chop();
        tomato.chop();
        cheese.chop();

        System.out.println("Before cooking:");
        System.out.println("  Dough  = " + dough.getState());
        System.out.println("  Tomato = " + tomato.getState());
        System.out.println("  Cheese = " + cheese.getState());

        // 3) Masukkan ke oven
        oven.addIngredient(dough);
        oven.addIngredient(tomato);
        oven.addIngredient(cheese);

        oven.startCooking();

        // 4) Simulasi 12 detik
        for (int t = 1; t <= 20; t++) {
            oven.update(1.0);
            System.out.println("Tick " + t + " | cooking=" + oven.isCooking()
                    + " | time=" + oven.getCookTimeSeconds());
        }

        // 5) Ambil semua isi oven
        List<Preparable> hasil = oven.takeOutAll();

        System.out.println("\nAfter cooking:");
        for (Preparable p : hasil) {
            System.out.println("  " + p.getClass().getSimpleName());
        }

        // 6) Deteksi jenis pizza
        PizzaType type = PizzaRecipeChecker.detectPizza(hasil);
        System.out.println("\nDetected type = " + type);

        // 7) Buat Dish pizza-nya
        Dish dish = PizzaRecipeChecker.createPizzaDish(type, hasil);
        if (dish != null) {
            System.out.println("Dish name        = " + dish.getName());
            System.out.println("Components count = " + dish.getComponents().size());
        } else {
            System.out.println("Dish is null (resep tidak cocok).");
        }
    }
}
