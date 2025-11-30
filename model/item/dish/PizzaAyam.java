package model.item.dish; 

import java.util.List;
import model.enums.ItemLocation;
import model.item.Dish; 
import model.item.Preparable; 

public class PizzaAyam extends Dish{
    public PizzaAyam(ItemLocation location){
        super(model.item.dish.Order.PizzaType.AYAM, location); 
    }

    public PizzaAyam(ItemLocation location, List<Preparable> components){
        this(location); 
        if (components != null){
            for (Preparable p : components){
                addComponent(p);
            }
        }
    }
}