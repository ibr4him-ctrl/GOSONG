package model.item.dish;

import model.item.Dish;
import model.item.ingredient.pizza.*;
import model.item.ingredient.Ingredient;

public class pizzaAyam extends Dish {
    
    public pizzaAyam() {
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
                if (((Dough) component).getState() != Ingredient.State.COOKED) {
                    return false;
                }
            } else if (component instanceof Cheese) {
                hasCheese = true;
                if (((Cheese) component).getState() != Ingredient.State.COOKED) {
                    return false;
                }
            } else if (component instanceof Chicken) {
                hasChicken = true;
                if (((Chicken) component).getState() != Ingredient.State.COOKED) {
                    return false;
                }
            }
        }
        
        return hasDough && hasCheese && hasChicken;
    }
}
