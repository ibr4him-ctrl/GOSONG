package model.station;

import model.chef.Chef;
import model.enums.IngredientState;
import model.item.Item;
//import model.item.Preparable;
import model.item.ingredient.Ingredient;

public class CuttingStation extends Station {

    private Ingredient currentIngredient;
    private double cutProgressSeconds;
    private boolean cutting;

    private static final double CUT_TIME = 3.0; //3 detik

    public CuttingStation(int x, int y) {
        super(x, y,"Cutting");
        this.currentIngredient = null;
        this.cutProgressSeconds = 0.0;
        this.cutting = false;
    }

    @Override
    public String getSymbol() {
        return "C";
    }
    @Override
    public boolean interact(Chef chef) {
        Item hand = chef.getHeldItem();
        if (currentIngredient == null) {
            Ingredient target = null;
            if (hand instanceof Ingredient ing &&
                ing.getState() == IngredientState.RAW) {
                target = ing;
                chef.setHeldItem(null);
            }

            else if (hand == null && itemOnStation instanceof Ingredient ing2 &&
                     ing2.getState() == IngredientState.RAW) {
                target = ing2;
            } else {

                return false;
            }

            itemOnStation = target;
            currentIngredient = target;
            cutting = true;
            return true;
        }
        if (currentIngredient != null &&
            currentIngredient.getState() == IngredientState.RAW &&
            !cutting) {
            cutting = true;
            return true;
        }

        return false;
    }

    public void update(double deltaSeconds) {
        if (!cutting || currentIngredient == null) return;

        cutProgressSeconds += deltaSeconds;

        if (cutProgressSeconds >= CUT_TIME) {
            currentIngredient.chop();
            cutting = false;
            cutProgressSeconds = CUT_TIME;
        }
    }

    public void pauseCutting() {
        cutting = false;
    }

    public boolean isCutting() {
        return cutting;
    }

    public double getCutProgressSeconds() {
        return cutProgressSeconds;
    }
}