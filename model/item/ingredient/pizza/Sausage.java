package model.item.ingredient.pizza; 

import model.enums.ItemLocation; 
import model.item.ingredient.Ingredient;

public class Sausage extends Ingredient{
    public Sausage(){
        this(ItemLocation.INGREDIENT_STORAGE); 
    }

    public Sausage(ItemLocation location){
        super("Sausage", location); 
    }
}