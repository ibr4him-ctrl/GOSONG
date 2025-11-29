package model.interfaces; 

import model.item.Preparable;

public interface CookingDevice{
    boolean isPortable(); 

    int capacity(); 

    boolean canAccept(Preparable ingredient); 

    void addIngredient(Preparable Ingredient); 

    void startCooking(); 
}