package model.item.ingredient.pizza;

import model.enums.IngredientState;

public class Tomato extends Item {
    private IngredientState state;
    private boolean isCooked;
    private boolean isBurned;

    public Tomato() {
        super("Tomato");
        this.state = IngredientState.UNCHOPPED; // Awalnya tomat belum dipotong
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

    @Override
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
