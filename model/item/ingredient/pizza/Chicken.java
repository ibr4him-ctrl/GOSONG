package model.item.ingredient.pizza; 

import model.enums.ItemLocation; 
import model.item.ingredient.Ingredient;

public class Chicken extends Ingredient{
    public Chicken(){
        this(ItemLocation.INGREDIENT_STORAGE); 
    }

    public Chicken(ItemLocation location){
        super("Chicken", location); 
    }
}