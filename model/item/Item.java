package model.item;

import model.enums.ItemLocation; 
import model.enums.ItemType; 

public abstract class Item {
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
    public ItemType getItemType() {
        return type;
    }
    
    public void setLocation(ItemLocation location) { 
        this.location = location; 
    }
    
    public void setEdible(boolean edible) { 
        this.isEdible = edible; 
    }
    
    public void setClean(boolean clean) { 
        this.isClean = clean; 
    }
    public void markDirty(){
        this.isClean = false; 
    }
    public void wash(){
        this.isClean = true; 
    }
    public String toString(){
        return String.format(
            "%s{name='%s', type=%s, location=%s, edible=%s, clean=%s}",
            getClass().getSimpleName(), name, type, location, isEdible, isClean
        );
    }
}
