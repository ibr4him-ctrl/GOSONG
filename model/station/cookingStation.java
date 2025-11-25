package model.station;

public class cookingStation extends station{
    private KitchenUtensil utensil;
    private float cookTimer;
    private float burnTimer;
    
    public void interact(Chef chef) {
        if (chef.getHeldItem() instanceof Ingredient && utensil.isEmpty()) {
            utensil.addIngredient((Ingredient) chef.getHeldItem());
            chef.putDownItem();
            cookTimer = 0;
        } else if (utensil.isReady() && chef.getHeldItem() == null) {
            chef.pickupItem(utensil.removeIngredient());
        }
    }
    
    public void update(float deltaTime) {
        if (!utensil.isEmpty()) {
            cookTimer += deltaTime;
            if (cookTimer >= utensil.getCookTime()) {
                utensil.getIngredient().setState(IngredientState.COOKED);
                burnTimer += deltaTime;
                if (burnTimer >= utensil.getBurnTime()) {
                    utensil.getIngredient().setState(IngredientState.BURNT);
                }
            }
        }
    }
}
