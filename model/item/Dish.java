package model.item;

import java.util.Collections; 
import java.util.ArrayList;
import java.util.List;

import model.enums.ItemLocation; 
import model.enums.ItemType;
import model.item.dish.Order;
import model.item.dish.Order.PizzaType;

public class Dish extends Item {
    private final PizzaType type;
    private List<Preparable> components;
    
    
    public Dish(PizzaType type, ItemLocation location) {
        super(type.getDisplayName(), ItemType.DISH, location); 
        this.type = type;
        this.components = new ArrayList<>();
        setEdible(true); 
    }
    public PizzaType getPizzaType() {
        return type;
    }
    // ----------------------------------------------------
    
    public List<Preparable> getComponents() {
        return Collections.unmodifiableList(components);
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