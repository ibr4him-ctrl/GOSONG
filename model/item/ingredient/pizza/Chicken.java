package model.item.ingredient.pizza;

import model.enums.IngredientState;
import model.item.Preparable;

public class Chicken extends Ingredient implements Preparable {
    
    public Chicken() {
        super();
    }
    
    @Override
    public boolean canBeChopped() {
        return getState() == State.RAW;
    }
    
    @Override
    public boolean canBeCooked() {
        return getState() == State.CHOPPED;
    }
    
    @Override
    public boolean canBePlacedOnPlate() {
        return getState() == State.COOKED;
    }
    
    @Override
    public void chop() {
        if (canBeChopped()) {
            setState(State.CHOPPED);
        }
    }
    
    @Override
    public void cook() {
        if (getState() == State.CHOPPED) {
            setState(State.COOKING);
        } else if (getState() == State.COOKING) {
            setState(State.COOKED);
        } else if (getState() == State.COOKED) {
            setState(State.BURNED);
        }
    }
}
