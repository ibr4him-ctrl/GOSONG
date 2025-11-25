package model.item;

import java.util.ArrayList;
import java.util.List;

public class Dish extends Item {
    
    private List<Preparable> components;
    public Dish(String name, ItemLocation location) {
        super(name, ItemType.DISH, location); 
        this.components = new ArrayList<>();
    }
    // ----------------------------------------------------
    
    public List<Preparable> getComponents() {
        return components;
    }
    
    public void addComponent(Preparable component) {
        this.components.add(component);
    }
    
    public void removeComponent(Preparable component) {
        this.components.remove(component);
    }
    
    public void clearComponents() {
        this.components.clear();
    }
    
    public boolean isValid() {
        return getName() != null && !components.isEmpty();
    }
}