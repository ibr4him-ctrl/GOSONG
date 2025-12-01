package model.item.dish; 

import java.util.List;
import model.enums.ItemLocation;
import model.item.Dish; 
import model.item.Preparable; 

public class PizzaMargherita extends Dish{
    public PizzaMargherita(ItemLocation location){
        super(model.item.dish.Order.PizzaType.MARGHERITA, location); 
    }

    public PizzaMargherita(ItemLocation location, List<Preparable> components){
        this(location); 
        if (components != null){
            for (Preparable p : components){
                addComponent(p);
            }
        }
    }
}