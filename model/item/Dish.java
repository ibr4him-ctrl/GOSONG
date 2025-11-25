package model.item;

import java.util.ArrayList;
import java.util.List;

public class Dish extends Item {
    private String name;
    private List<Preparable> components;
    
    public Dish() {
        this.components = new ArrayList<>();
    }
    
    // Method setName berada di sini
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
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
    
    // Validasi: dish tidak valid jika tidak ada dalam resep
    public boolean isValid() {
        // Implementasi validasi berdasarkan resep
        return name != null && !components.isEmpty();
    }
}
