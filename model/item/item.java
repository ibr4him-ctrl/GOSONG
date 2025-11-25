package model.item;

public class Item {
    public enum ItemType {
        INGREDIENT,
        UTENSIL,
        DISH
    }

    public enum ItemLocation {
        COUNTER,
        STOVE,
        CUTTING_BOARD,
        PLATE,
        INVENTORY,
        FLOOR
    }
    private String name;
    private ItemType type;
    private ItemLocation location;
    private boolean isEdible;
    private boolean isClean;
    
    public Item(String name, ItemType type, ItemLocation location) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.isEdible = false;
        this.isClean = true;
    }
    
    // Getters
    public String getName() { 
        return name; 
    }
    
    public ItemType getType() { 
        return type; 
    }
    
    public ItemLocation getLocation() { 
        return location; 
    }
    
    public boolean isEdible() { 
        return isEdible; 
    }
    
    public boolean isClean() { 
        return isClean; 
    }
    
    // Setters
    public void setLocation(ItemLocation location) { 
        this.location = location; 
    }
    
    public void setEdible(boolean edible) { 
        this.isEdible = edible; 
    }
    
    public void setClean(boolean clean) { 
        this.isClean = clean; 
    }
}
