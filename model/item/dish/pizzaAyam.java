package model.dish;

<<<<<<< HEAD
import model.ingredient.pizza.Cheese;
import model.ingredient.pizza.Chicken;
import model.ingredient.pizza.Dough;
import model.item.Dish;

public class PizzaAyam extends Dish {
=======
public class pizzaAyam {
>>>>>>> 4a05a7e78a36f05ac9538c33f0f45371c4d3f6b8
    
    public PizzaAyam() {
        super();
        setName("Pizza Ayam");
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Pizza Ayam terdiri dari: Dough, Cheese, Chicken
        addComponent(new Dough());
        addComponent(new Cheese());
        addComponent(new Chicken());
    }
    
    @Override
    public boolean isValid() {
        // Validasi bahwa semua komponen ada dan dalam state yang benar
        if (getComponents().size() != 3) {
            return false;
        }
        
        // Cek apakah semua ingredient ada dan dalam state COOKED
        boolean hasDough = false;
        boolean hasCheese = false;
        boolean hasChicken = false;
        
        for (var component : getComponents()) {
            if (component instanceof Dough) {
                hasDough = true;
                if (((Dough) component).getState() != model.ingredient.Ingredient.State.COOKED) {
                    return false;
                }
            } else if (component instanceof Cheese) {
                hasCheese = true;
                if (((Cheese) component).getState() != model.ingredient.Ingredient.State.COOKED) {
                    return false;
                }
            } else if (component instanceof Chicken) {
                hasChicken = true;
                if (((Chicken) component).getState() != model.ingredient.Ingredient.State.COOKED) {
                    return false;
                }
            }
        }
        
        return hasDough && hasCheese && hasChicken;
    }
}