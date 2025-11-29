package model.station;

import model.chef.Chef;
import model.enums.IngredientState;
import model.item.Item;
import model.item.ingredient.Ingredient;

public class CuttingStation extends Station {

    private Ingredient currentIngredient;
    private double cutProgressSeconds;
    private boolean cutting;
    private Chef currentChef; 

    private static final double CUT_TIME = 3.0; //3 detik

    public CuttingStation(int x, int y) {
        super(x, y,"Cutting");
        this.currentIngredient = null;
        this.cutProgressSeconds = 0.0;
        this.cutting = false;
        this.currentChef = null; 
    }

    @Override
    public String getSymbol() {
        return "C";
    }
    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) return false; 

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
            currentChef = chef; 
            chef.setBusy(true);
            return true;
        }
        if (currentIngredient != null &&
            currentIngredient.getState() == IngredientState.RAW &&
            !cutting) {
            cutting = true;
            currentChef =  chef; 
            chef.setBusy(true);
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

            if (currentChef != null){
                currentChef.setBusy(false);
            }
        }
    }

    public void pauseCutting() {
        cutting = false;
        if (currentChef != null){
            currentChef.setBusy(false);
            currentChef = null; 
        }
    }

    public boolean isCutting() {
        return cutting;
    }

    public double getCutProgressSeconds() {
        return cutProgressSeconds;
    }
    
   // ===== helper untuk progress bar di CLI =====

    /** 0.0 .. 1.0 */
    public double getProgressRatio() {
        if (CUT_TIME <= 0) return 0.0;
        return Math.min(1.0, cutProgressSeconds / CUT_TIME);
    }

    /** 0 .. 100 % */
    public int getProgressPercent() {
        return (int) Math.round(getProgressRatio() * 100.0);
    }

    /** e.g. "[##########----------] 50%" */
    public String getProgressBar(int width) {
        double ratio = getProgressRatio();
        int filled = (int) Math.round(ratio * width);
        if (filled > width) filled = width;

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < width; i++) {
            sb.append(i < filled ? '#' : '-');
        }
        sb.append("] ").append(getProgressPercent()).append('%');
        return sb.toString();
    }
}