package model.dish;

<<<<<<< HEAD
import model.ingredient.pizza.Cheese;
import model.ingredient.pizza.Dough;
import model.ingredient.pizza.Tomato;
import model.item.Dish;

public class PizzaMargherita extends Dish {
=======
public class pizzaMargherita {
>>>>>>> 4a05a7e78a36f05ac9538c33f0f45371c4d3f6b8
    
    public PizzaMargherita() {
        super();
        setName("Pizza Margherita");
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Pizza Margherita terdiri dari: Dough, Cheese, Tomato
        addComponent(new Dough());
        addComponent(new Cheese());
        addComponent(new Tomato());
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
        boolean hasTomato = false;
        
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
            } else if (component instanceof Tomato) {
                hasTomato = true;
                if (((Tomato) component).getState() != model.ingredient.Ingredient.State.COOKED) {
                    return false;
                }
            }
        }
        
        return hasDough && hasCheese && hasTomato;
    }
}