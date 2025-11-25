package dish;

import ingredient.pizza.Cheese;
import ingredient.pizza.Dough;
import ingredient.pizza.Sausage;
import item.Dish;

public class PizzaSosis extends Dish {
    
    public PizzaSosis() {
        super();
        setName("Pizza Sosis");
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Pizza Sosis terdiri dari: Dough, Cheese, Sausage
        addComponent(new Dough());
        addComponent(new Cheese());
        addComponent(new Sausage());
    }
    
    @Override
    public boolean isValid() {
        // Validasi bahwa semua komponen ada dan dalam state yang benar
        if (getComponents().size() != 3) {
            return false;
        }
        
        // Semua komponen harus sudah dimasak (COOKED)
        for (var component : getComponents()) {
            if (component instanceof ingredient.Ingredient) {
                ingredient.Ingredient ing = (ingredient.Ingredient) component;
                if (ing.getState() != ingredient.Ingredient.State.COOKED) {
                    return false;
                }
            }
        }
        
        return super.isValid();
    }
}