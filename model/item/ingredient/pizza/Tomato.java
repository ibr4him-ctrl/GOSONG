package model.item.ingredient.pizza;


import model.enums.ItemLocation; 
import model.item.ingredient.Ingredient;

public class Tomato extends Ingredient{
    public Tomato(){
        this(ItemLocation.INGREDIENT_STORAGE); 
    }

    public Tomato(ItemLocation location){
        super("Tomato",location); 
    }
}