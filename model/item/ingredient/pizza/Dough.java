package model.item.ingredient.pizza;

import model.enums.IngredientState;
import model.item.item;

public class Dough extends item {
    private IngredientState state;
    private boolean isCooked;
    private boolean isBurned;

    public Dough() {
        super("Dough");
        this.state = IngredientState.UNCHOPPED; // Awalnya adonan belum dipotong
        this.isCooked = false;
        this.isBurned = false;
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

    public String toString() {
        String status = "";
        if (isBurned) {
            status = "burned ";
        } else if (isCooked) {
            status = "cooked ";
        }
        return status + (state == IngredientState.CHOPPED ? "chopped " : "") + super.toString();
    }
}
