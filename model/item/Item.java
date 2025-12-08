package model.item;

import model.enums.ItemLocation; 
import model.enums.ItemType; 

public abstract class Item { // abstract berarti dia ga bisa dibikin langsung mesti ada turunannya 
    private String name; //nama benda
    private ItemType type; //jenis: ingredient, plate, dsb
    private ItemLocation location; // lokasi benda benda ditaro
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
