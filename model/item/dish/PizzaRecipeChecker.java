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
    private PizzaRecipeChecker(){
        //utility class, jangan di-instaniaasi 
    } 
    //deteksi ini pizza apa ddari ist ingredient yang sudah dimasak 
    public static PizzaType detectPizza(List<Preparable> components){
        if (components == null || components.isEmpty()){
            return null; 
        }

        boolean hasDough = false; 
        boolean hasTomato = false; 
        boolean hasCheese = false; 
        boolean hasChicken = false; 
        boolean hasSausage = false; 

        int ingredientCount = 0; 

        for (Preparable p : components){
            if (!(p instanceof Ingredient)){
                continue; 
            }

            Ingredient ing  = (Ingredient) p; 
            ingredientCount++; 

            // maunya bahan yang keluar dari oven sudah cooked 
            IngredientState state = ing.getState(); 
            if (state != IngredientState.COOKED){
                return null; 
            }

            if (ing instanceof Dough) hasDough = true; 
            if (ing instanceof Tomato) hasTomato = true; 
            if (ing instanceof Cheese) hasCheese = true; 
            if (ing instanceof Chicken) hasChicken = true;
            if (ing instanceof Sausage) hasSausage = true; 
        }
        // Margherita : Dough + Tomato + Cheese 
        if (ingredientCount == 3 && hasDough && hasTomato && hasCheese && !hasChicken && !hasSausage){
            return PizzaType.MARGHERITA; 
        }

        // Sosis : Dough + Tomato + Cheese + Sausage 
        if (ingredientCount == 4 && hasDough && hasTomato && hasCheese && !hasChicken && hasSausage){
            return PizzaType.SOSIS; 
        }
            
        //Ayam : Dough + Tomato + Cheese + Ayam 
        if (ingredientCount == 4 && hasDough && hasTomato && hasCheese && hasChicken && !hasSausage){
            return PizzaType.AYAM; 
        }

        return null; 
    }

    //bikin objek dish pizza konkret dari tipe + komponennya 
    public static Dish createPizzaDish(PizzaType type, List<Preparable> components){
        if (type == null){
            return null; 
        }

        Dish dish; 

        switch (type){
            case MARGHERITA -> dish = new PizzaMargherita(ItemLocation.PLATING); 
            case AYAM -> dish = new PizzaAyam(ItemLocation.PLATING); 
            case SOSIS -> dish = new PizzaSosis(ItemLocation.PLATING); 

            default -> {return null; }
        }

        if (components != null){
            for (Preparable p : components){
                dish.addComponent(p);
            }
        }

        return dish; 
    }
}

