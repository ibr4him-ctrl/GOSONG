package model.item.ingredient.pizza; 

import model.enums.IngredientState;
import model.enums.ItemLocation; 
import model.item.ingredient.Ingredient;

public class Dough extends Ingredient{
    public Dough(){
        this(ItemLocation.INGREDIENT_STORAGE); 
    }

    public Dough(ItemLocation location){
        super("Dough", location); 
    }

    @Override
    public boolean canBeChopped() {
        return false;
    }
    @Override
    public void chop() {
        throw new IllegalStateException("Dough tidak bisa dipotong!");
    }

    // Optional tambahan: Dough tidak pernah jadi CHOPPED, tapi boleh dimasak
    @Override
    public boolean canBeCooked() {
        return getState() == IngredientState.RAW;
    }
}