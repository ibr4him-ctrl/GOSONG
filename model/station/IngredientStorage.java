package model.station;

import model.chef.Chef;
import model.item.Item;
import model.item.ingredient.Ingredient;
import model.enums.IngredientState;

public class IngredientStorage extends Station {
    
    private Class<? extends Ingredient> ingredientType;
    private String ingredientName;

    public IngredientStorage(int x, int y, Class<? extends Ingredient> ingredientType, String ingredientName) {
        super(x, y, "Ingredient Storage");
        this.ingredientType = ingredientType;
        this.ingredientName = ingredientName;
    }
    
    @Override
    public String getSymbol() {
        return "I";
    }

    @Override
    public boolean interact(Chef chef) {
        if (!isAdjacentTo(chef)) {
            System.out.println("❌ Chef terlalu jauh dari ingredient storage!");
            return false;
        }
        
        Item hand = chef.getHeldItem();
        
        if (hand == null && itemOnStation != null) {
            chef.setHeldItem(itemOnStation);
            
            itemOnStation = null;
            return true;
        }

        if (hand == null && itemOnStation == null) {
            Ingredient newIngredient = createNewIngredient();
            
            if (newIngredient != null) {
                chef.setHeldItem(newIngredient);
                return true;
            } else {
                return false;
            }
        }
        
        if (hand != null) {
            if (itemOnStation != null) {
                System.out.println("Sudah ada item diatasnya!");
                return false;
            }
            
            itemOnStation = hand;
            chef.setHeldItem(null);
            return true;
        }
        
        return false;
    }
    
    private Ingredient createNewIngredient() {
        try {
            Ingredient newIng = ingredientType.getDeclaredConstructor().newInstance();
            if (newIng.getState() != IngredientState.RAW) {
                newIng.setState(IngredientState.RAW);
            }
            return newIng;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Item takeItemFromTop() {
        Item temp = itemOnStation;
        itemOnStation = null;
        return temp;
    }
    public boolean placeItemOnTop(Item item) {
        if (itemOnStation != null) {
            return false; 
        }
        itemOnStation = item;
        return true;
    }
    
    public boolean hasItemOnTop() {
        return itemOnStation != null;
    }
    
    public Class<? extends Ingredient> getIngredientType() {
        return ingredientType;
    }
    
    /**
     * Get nama ingredient
     */
    public String getIngredientName() {
        return ingredientName;
    }
    
    /**
     * Validasi ingredient
     */
    public boolean isValidIngredient(Item item) {
        return item instanceof Ingredient && 
               item.getClass() == ingredientType;
    }

    //butuh ga ya
    public String getTooltip() {
        StringBuilder sb = new StringBuilder();
        sb.append(ingredientName).append(" Storage\n");
        sb.append("Stock: Unlimited (RAW)\n");
        
        if (itemOnStation != null) {
            sb.append("Item on top: ").append(itemOnStation.getName());
            if (itemOnStation instanceof Ingredient ing) {
                sb.append(" (").append(ing.getState()).append(")");
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("IngredientStorage{ingredient=%s, pos=(%d,%d), itemOnTop=%s}",
            ingredientName, posX, posY, 
            itemOnStation != null ? itemOnStation.getName() : "none");
    }
}