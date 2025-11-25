package model.item.dish.pizza;

import model.item.dish.Dish;
import model.item.ingredient.Preparable;
import model.item.ingredient.pizza.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pizza Sosis - Sausage Pizza
 * Recipe: Dough (Chopped) + Tomato (Chopped) + Cheese (Chopped) + Sausage (Chopped)
 * Note: Must be cooked in Oven after assembly
 */
public class PizzaSosis extends Dish {

    public PizzaSosis() {
        super("Pizza Sosis", createComponents());
    }

    /**
     * Create the ingredient components for Pizza Sosis
     * All ingredients must be in CHOPPED state before assembly
     */
    private static List<Preparable> createComponents() {
        List<Preparable> components = new ArrayList<>();
        
        Dough dough = new Dough();
        dough.chop();
        components.add(dough);
        
        Tomato tomato = new Tomato();
        tomato.chop();
        components.add(tomato);
        
        Cheese cheese = new Cheese();
        cheese.chop();
        components.add(cheese);
        
        Sausage sausage = new Sausage();
        sausage.chop();
        components.add(sausage);
        
        return components;
    }

    /**
     * Validate if the given ingredients match Pizza Sosis recipe
     * All ingredients must be in CHOPPED state
     */
    public static boolean isValidRecipe(List<Preparable> ingredients) {
        if (ingredients.size() != 4) return false;
        
        boolean hasDough = false;
        boolean hasTomato = false;
        boolean hasCheese = false;
        boolean hasSausage = false;

        for (Preparable ing : ingredients) {
            if (ing instanceof Dough && ing.canBePlacedOnPlate()) {
                hasDough = true;
            } else if (ing instanceof Tomato && ing.canBePlacedOnPlate()) {
                hasTomato = true;
            } else if (ing instanceof Cheese && ing.canBePlacedOnPlate()) {
                hasCheese = true;
            } else if (ing instanceof Sausage && ing.canBePlacedOnPlate()) {
                hasSausage = true;
            }
        }
        
        return hasDough && hasTomato && hasCheese && hasSausage;
    }

    @Override
    public String toString() {
        return "Pizza Sosis [" + getComponentsString() + "]";
    }
    
    private String getComponentsString() {
        StringBuilder sb = new StringBuilder();
        List<Preparable> comps = getComponents();
        for (int i = 0; i < comps.size(); i++) {
            sb.append(comps.get(i).toString());
            if (i < comps.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}