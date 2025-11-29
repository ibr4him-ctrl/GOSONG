package model.item.ingredient.pizza; 

import model.enums.ItemLocation; 
import model.item.ingredient.Ingredient;

public class Cheese extends Ingredient{
    public Cheese(){
        this(ItemLocation.INGREDIENT_STORAGE); 
    }

    public Cheese(ItemLocation location){
        super("Cheese", location); 
    }
}