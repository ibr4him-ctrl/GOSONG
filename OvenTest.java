import java.util.List;
import model.enums.ItemLocation;
import model.item.Preparable;
import model.item.ingredient.pizza.Cheese;
import model.item.ingredient.pizza.Dough;
import model.item.ingredient.pizza.Tomato;
import model.item.utensils.Oven;

public class OvenTest {
    public static void main(String[] args) {

        Oven oven = new Oven();  // pakai default constructor kamu

        // Bikin bahan (sesuaikan constructor Dough/Tomato/Cheese kamu ya)
        Dough dough   = new Dough(ItemLocation.INGREDIENT_STORAGE);
        Tomato tomato = new Tomato(ItemLocation.INGREDIENT_STORAGE);
        Cheese cheese = new Cheese(ItemLocation.INGREDIENT_STORAGE);

        // Harus CHOPPED dulu supaya canAccept() = true
        dough.chop();
        tomato.chop();
        cheese.chop();

        // Masukin ke oven
        oven.addIngredient(dough);
        oven.addIngredient(tomato);
        oven.addIngredient(cheese);

        oven.startCooking();

        // Simulasi 12 detik
        for (int t = 1; t <= 12; t++) {
            oven.update(1.0);
            System.out.println("Tick " + t + " | cooking=" + oven.isCooking() + " | time=" + oven.getCookTimeSeconds());
        }

        // Setelah matang (>= 12 detik) → ambil semua isi
        List<Preparable> hasil = oven.takeOutAll();

        System.out.println("\nSetelah keluar dari oven:");
        for (Preparable p : hasil) {
            if (p instanceof Dough d) {
                System.out.println("Dough state  = " + d.getState());
            } else if (p instanceof Tomato t) {
                System.out.println("Tomato state = " + t.getState());
            } else if (p instanceof Cheese c) {
                System.out.println("Cheese state = " + c.getState());
            }
        }
    }
}
