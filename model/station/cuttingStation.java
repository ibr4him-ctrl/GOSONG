package model.station;

import model.enums.IngredientState;

public class cuttingStation extends station {
    public void interact (Chef chef){
        if (chef.getHeldItem() != null && placedItem == null) {
            placedItem = chef.getHeldItem();
            chef.putDownItem();
        } else if (placedItem instanceof Ingredient) {
            Ingredient ingredient = (Ingredient) placedItem;
            ingredient.setState(IngredientState.CHOPPED);
        } else if (placedItem != null && chef.getHeldItem() == null) {
            chef.pickupItem(placedItem);
            placedItem = null;
        }
    }
        
    public void update(float deltaTime) { }
}