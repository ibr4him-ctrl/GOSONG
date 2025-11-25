package model.item.ingredient.pizza;

import model.enums.IngredientState;
import model.item.Item;

public class Cheese extends Item {
    private IngredientState state;
    private boolean isCooked;
    private boolean isBurned;

    public Cheese() {
        super("Cheese", Item.ItemType.INGREDIENT, Item.ItemLocation.COUNTER); 
        this.state = IngredientState.UNCHOPPED; 
        this.isCooked = false;
        this.isBurned = false;
        this.setEdible(true); 
    }

    public void chop() {
        if (state == IngredientState.UNCHOPPED) {
            state = IngredientState.CHOPPED;
        }
    }

    public void cook() {
        if (!isBurned) {
            if (isCooked) {
                isBurned = true;
                isCooked = false;
                this.setEdible(false); 
            } else {
                isCooked = true;
            }
        }
    }

    public IngredientState getState() {
        return state;
    }

    public boolean isChopped() {
        return state == IngredientState.CHOPPED;
    }

    public boolean isCooked() {
        return isCooked && !isBurned;
    }

    public boolean isBurned() {
        return isBurned;
    }

    @Override
    public String toString() {
        String status = "";
        if (isBurned) {
            status = "burned ";
        } else if (isCooked) {
            // Untuk keju, "cooked" biasanya berarti "melted"
            status = "melted "; 
        }
        
        return status + (state == IngredientState.CHOPPED ? "grated " : "") + super.getName();
    }
}