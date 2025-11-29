package model.item.ingredient.pizza; 

import model.enums.ItemLocation; 
import model.item.ingredient.Ingredient;

public class Dough extends Ingredient{
    public Dough(){
        this(ItemLocation.INGREDIENT_STORAGE); 
    }

    public Dough(ItemLocation location){
        super("Dough", location); 
    }
}