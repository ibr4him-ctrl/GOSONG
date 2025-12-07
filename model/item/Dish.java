package model.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.enums.ItemLocation; 
import model.enums.ItemType;
import model.item.dish.Order.PizzaType;

public class Dish extends Item implements Preparable {
    private final PizzaType type;
    private List<Preparable> components;
    
    
    public Dish(PizzaType type, ItemLocation location) {
        super(type.getDisplayName(), ItemType.DISH, location); 
        this.type = type;
        this.components = new ArrayList<>();
        setEdible(true); 
    }

    // ----------------------------------------------------
    public PizzaType getPizzaType() {
        return type;
    }

    public List<Preparable> getComponents() {
        return Collections.unmodifiableList(components);
    }
    
    public void addComponent(Preparable component) {
        this.components.add(component);
    }
    
    public void removeComponent(Preparable component) {
        this.components.remove(component);
    }

    // ====== implementasi Preparable untuk "hidangan akhir" ======

    @Override
    public boolean canBeChopped() {
        // Dish final tidak boleh dicacah lagi
        return false;
    }

    @Override
    public boolean canBeCooked() {
        // Sudah matang, tidak bisa dimasak lagi
        return false;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        // Justru ini yang penting: Dish BOLEH ditaruh di plate
        return true;
    }

    @Override
    public void chop() {
        // no-op: tidak melakukan apa-apa, atau boleh kasih log
        System.out.println("[Dish] chop() dipanggil tapi dish sudah final.");
    }

    @Override
    public void cook() {
        // no-op: tidak melakukan apa-apa
        System.out.println("[Dish] cook() dipanggil tapi dish sudah final.");
    }

    @Override
    public String toString() {
        return "Dish{" + getName() + ", type=" + type + "}";
    }
    
    public void clearComponents() {
        this.components.clear();
    }
    
    public boolean isValid() {
        return getName() != null && !components.isEmpty();
    }
}